package se.sundsvall.comfactfacade.api;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.api.model.Account;
import se.sundsvall.comfactfacade.service.AccountService;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class AccountResourceFailureTest {

	@MockitoBean
	private AccountService accountServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	private static Stream<Arguments> provideBadCreateRequests() {
		return Stream.of(
			Arguments.of(Account.builder().withClientId("someClientId").withClientSecret("someClientSecret").build(), "accountKey", "must not be blank"),
			Arguments.of(Account.builder().withAccountKey("some key!").withClientId("someClientId").withClientSecret("someClientSecret").build(), "accountKey",
				"must only contain letters, digits, '.', '_' and '-'"),
			Arguments.of(Account.builder().withAccountKey("valid-key").withClientSecret("someClientSecret").build(), "clientId", "must not be blank"),
			Arguments.of(Account.builder().withAccountKey("valid-key").withClientId("someClientId").build(), "clientSecret", "must not be blank"));
	}

	@ParameterizedTest
	@MethodSource("provideBadCreateRequests")
	void createAccountWithInvalidBody(final Account account, final String field, final String message) {
		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/accounts", MUNICIPALITY_ID)
			.contentType(APPLICATION_JSON)
			.bodyValue(account)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.contains(tuple(field, message));
		verifyNoInteractions(accountServiceMock);
	}

	@Test
	void getAccountsWithInvalidMunicipalityId() {
		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/accounts", "not-valid")
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.contains(tuple("getAccounts.municipalityId", "not a valid municipality ID"));
		verifyNoInteractions(accountServiceMock);
	}
}
