package se.sundsvall.comfactfacade.integration.comfact.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import se.sundsvall.comfactfacade.service.AccountCredentials;

import static org.assertj.core.api.Assertions.assertThat;

class ComfactAccountContextTest {

	@AfterEach
	void cleanup() {
		ComfactAccountContext.remove();
	}

	@Test
	void setGetAndRemove() {
		final var credentials = new AccountCredentials("id", "clientId", "clientSecret", "comfactAccountId");

		assertThat(ComfactAccountContext.get()).isEmpty();

		ComfactAccountContext.set(credentials);
		assertThat(ComfactAccountContext.get()).contains(credentials);

		ComfactAccountContext.remove();
		assertThat(ComfactAccountContext.get()).isEmpty();
	}
}
