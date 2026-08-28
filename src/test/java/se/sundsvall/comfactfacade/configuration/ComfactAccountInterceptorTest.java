package se.sundsvall.comfactfacade.configuration;

import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import se.sundsvall.comfactfacade.integration.comfact.configuration.ComfactAccountContext;
import se.sundsvall.comfactfacade.service.AccountCredentials;
import se.sundsvall.comfactfacade.service.AccountService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
import static se.sundsvall.comfactfacade.api.ApiConstants.ACCOUNT_KEY_HEADER;

@ExtendWith(MockitoExtension.class)
class ComfactAccountInterceptorTest {

	@Mock
	private AccountService accountServiceMock;

	@InjectMocks
	private ComfactAccountInterceptor interceptor;

	@AfterEach
	void cleanup() {
		ComfactAccountContext.remove();
	}

	@Test
	void preHandleResolvesAccountFromPathAndHeader() {
		// Arrange
		final var credentials = new AccountCredentials("id", "clientId", "clientSecret", "comfactAccountId");
		final var request = new MockHttpServletRequest();
		request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("municipalityId", "2281"));
		request.addHeader(ACCOUNT_KEY_HEADER, "social-services");
		when(accountServiceMock.resolveAccount("2281", "social-services")).thenReturn(credentials);

		// Act
		final var result = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

		// Assert
		assertThat(result).isTrue();
		assertThat(ComfactAccountContext.get()).contains(credentials);
	}

	@Test
	void preHandleWithoutHeaderAndPathVariables() {
		// Arrange
		final var credentials = new AccountCredentials("id", "clientId", "clientSecret", null);
		when(accountServiceMock.resolveAccount(null, null)).thenReturn(credentials);

		// Act
		final var result = interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object());

		// Assert
		assertThat(result).isTrue();
		assertThat(ComfactAccountContext.get()).contains(credentials);
	}

	@Test
	void afterCompletionRemovesContext() {
		// Arrange
		ComfactAccountContext.set(new AccountCredentials("id", "clientId", "clientSecret", null));

		// Act
		interceptor.afterCompletion(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object(), null);

		// Assert
		assertThat(ComfactAccountContext.get()).isEmpty();
	}
}
