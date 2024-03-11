package se.sundsvall.comfactfacade.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NotificationMessageTest {

	@Test
	void testConstructorAndGetters() {

		// Arrange
		final String subject = "someSubject";
		final String body = "someBody";
		final String language = "sv-SE";

		// Act
		final NotificationMessage testObject = new NotificationMessage(subject, body, language);

		// Assert
		assertThat(testObject.subject()).isEqualTo(subject);
		assertThat(testObject.body()).isEqualTo(body);
		assertThat(testObject.language()).isEqualTo(language);

	}

}
