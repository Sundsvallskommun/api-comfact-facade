package se.sundsvall.comfactfacade.api;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.comfactfacade.Application;
import se.sundsvall.comfactfacade.api.model.Account;
import se.sundsvall.comfactfacade.service.AccountService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.comfactfacade.Constants.MUNICIPALITY_ID;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
@AutoConfigureWebTestClient
class AccountResourceTest {

	@MockitoBean
	private AccountService accountServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	private final ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

	@Test
	void getAccounts() {
		// Arrange
		when(accountServiceMock.getAccounts(MUNICIPALITY_ID)).thenReturn(List.of(
			Account.builder().withId("1").build(),
			Account.builder().withId("2").build()));

		// Act
		final var result = webTestClient.get()
			.uri("/{municipalityId}/accounts", MUNICIPALITY_ID)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Account.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(result).extracting(Account::getId).containsExactly("1", "2");
		verify(accountServiceMock).getAccounts(MUNICIPALITY_ID);
	}

	@Test
	void getAccount() {
		// Arrange
		when(accountServiceMock.getAccount(MUNICIPALITY_ID, "id")).thenReturn(Account.builder().withId("id").build());

		// Act
		final var result = webTestClient.get()
			.uri("/{municipalityId}/accounts/{id}", MUNICIPALITY_ID, "id")
			.exchange()
			.expectStatus().isOk()
			.expectBody(Account.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo("id");
		verify(accountServiceMock).getAccount(MUNICIPALITY_ID, "id");
	}

	@Test
	void createAccount() {
		// Arrange. The body is sent as raw JSON since clientSecret is write-only and would be dropped when
		// serializing an Account instance.
		when(accountServiceMock.createAccount(eq(MUNICIPALITY_ID), any(Account.class))).thenReturn("generated-id");

		// Act & Assert
		webTestClient.post()
			.uri("/{municipalityId}/accounts", MUNICIPALITY_ID)
			.contentType(APPLICATION_JSON)
			.bodyValue(Map.of(
				"accountKey", "social-services",
				"comfactAccountId", "comfact-account-2",
				"clientId", "some-client-id",
				"clientSecret", "some-client-secret"))
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().valueEquals("Location", "/" + MUNICIPALITY_ID + "/accounts/generated-id")
			.expectBody().isEmpty();

		verify(accountServiceMock).createAccount(eq(MUNICIPALITY_ID), accountCaptor.capture());
		assertThat(accountCaptor.getValue().getAccountKey()).isEqualTo("social-services");
		assertThat(accountCaptor.getValue().getClientSecret()).isEqualTo("some-client-secret");
	}

	@Test
	void updateAccount() {
		// Arrange
		final var account = Account.builder()
			.withComfactAccountId("new-account-id")
			.build();

		// Act & Assert
		webTestClient.patch()
			.uri("/{municipalityId}/accounts/{id}", MUNICIPALITY_ID, "id")
			.contentType(APPLICATION_JSON)
			.bodyValue(account)
			.exchange()
			.expectStatus().isNoContent();

		verify(accountServiceMock).updateAccount(MUNICIPALITY_ID, "id", account);
	}

	@Test
	void deleteAccount() {
		// Act & Assert
		webTestClient.delete()
			.uri("/{municipalityId}/accounts/{id}", MUNICIPALITY_ID, "id")
			.exchange()
			.expectStatus().isNoContent();

		verify(accountServiceMock).deleteAccount(MUNICIPALITY_ID, "id");
	}
}
