package se.sundsvall.comfactfacade.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountCredentialsTest {

	@Test
	void accessors() {
		final var credentials = new AccountCredentials("id", "clientId", "clientSecret", "comfactAccountId");

		assertThat(credentials.id()).isEqualTo("id");
		assertThat(credentials.clientId()).isEqualTo("clientId");
		assertThat(credentials.clientSecret()).isEqualTo("clientSecret");
		assertThat(credentials.comfactAccountId()).isEqualTo("comfactAccountId");
	}

	@Test
	void toStringHidesClientSecret() {
		final var credentials = new AccountCredentials("id", "clientId", "super-secret-value", "comfactAccountId");

		assertThat(credentials).hasToString("AccountCredentials[id=id, clientId=clientId, clientSecret=[hidden], comfactAccountId=comfactAccountId]");
		assertThat(credentials.toString()).doesNotContain("super-secret-value");
	}
}
