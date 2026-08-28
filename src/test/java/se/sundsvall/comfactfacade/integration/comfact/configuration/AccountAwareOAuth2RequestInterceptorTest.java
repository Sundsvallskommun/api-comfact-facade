package se.sundsvall.comfactfacade.integration.comfact.configuration;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountAwareOAuth2RequestInterceptorTest {

	private final AccountAwareOAuth2RequestInterceptor interceptor = new AccountAwareOAuth2RequestInterceptor("http://token.url");

	@AfterEach
	void cleanup() {
		ComfactAccountContext.remove();
	}

	@Test
	void applyWithoutAccountContextThrows() {
		assertThatThrownBy(() -> interceptor.apply(new RequestTemplate()))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("No Comfact account credentials set on the current thread");
	}

	@Test
	void removeTokenWithoutDelegatesIsNoOp() {
		assertThatCode(() -> interceptor.removeToken("Bearer some-token")).doesNotThrowAnyException();
	}
}
