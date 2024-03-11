package se.sundsvall.comfactfacade.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IdentificationTest {


	@Test
	void testConstructorAndGetters() {

		// Arrange
		final String alias = "test";

		// Act
		final Identification testObject = new Identification(alias);

		// Assert
		assertThat(testObject.alias()).isEqualTo(alias);

	}

}
