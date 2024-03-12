package se.sundsvall.comfactfacade.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PartyTest {

	@Test
	void constructorAndGetter() {
		// Arrange
		final var name = "someName";
		final var email = "someEmail";
		final var phone = "somePhone";
		final var partyId = "somePartyId";
		final var title = "someTitle";
		final var personalNumber = "somePersonNumber";
		final var organization = "someOrganization";
		final var language = "someLanguage";
		final var identification = new Identification("someAlias");

		// Act
		final var result = new Party(name, partyId, title, personalNumber, email, phone, organization, language, identification);

		// Assert
		assertThat(result.name()).isEqualTo(name);
		assertThat(result.partyId()).isEqualTo(partyId);
		assertThat(result.title()).isEqualTo(title);
		assertThat(result.personalNumber()).isEqualTo(personalNumber);
		assertThat(result.email()).isEqualTo(email);
		assertThat(result.phoneNumber()).isEqualTo(phone);
		assertThat(result.organization()).isEqualTo(organization);
		assertThat(result.language()).isEqualTo(language);
		assertThat(result.identification()).isEqualTo(identification);
	}

}
