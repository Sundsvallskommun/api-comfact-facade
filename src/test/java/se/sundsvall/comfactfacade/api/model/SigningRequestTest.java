package se.sundsvall.comfactfacade.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class SigningRequestTest {

	@Test
	void testConstructorAndGetters() {
		// Arrange
		final var customerReference = "someCustomerReference";
		final var expires = OffsetDateTime.now();
		final var notificationMessage = new NotificationMessage("someSubject", "someBody", "sv-SE");
		final var party = new Party("someName", "somePartyId", "someTitle", "somePersonNumber", "someEmail", "somePhone",
			"some", "someLanguage", new Identification("someAlias"));
		final var document = new Document("someName", "someFileName", "someContentType", "someContent");
		final var additionalDocuments = List.of(document, new Document("someName", "someFileName", "someContentType", "someContent"));
		final var language = "someLanguage";

		// Act
		final var result = new SigningRequest(customerReference, expires, notificationMessage, party, party, party, document, additionalDocuments, language);

		// Assert
		assertThat(result.customerReference()).isEqualTo(customerReference);
		assertThat(result.expires()).isEqualTo(expires);
		assertThat(result.notificationMessage()).isEqualTo(notificationMessage);
		assertThat(result.signatory()).isEqualTo(party);
		assertThat(result.additionalParty()).isEqualTo(party);
		assertThat(result.initiator()).isEqualTo(party);
		assertThat(result.document()).isEqualTo(document);
		assertThat(result.additionalDocuments()).isEqualTo(additionalDocuments);
		assertThat(result.language()).isEqualTo(language);

	}

}
