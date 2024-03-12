package se.sundsvall.comfactfacade.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DocumentTest {
	
	@Test
	void constructorAndGetter() {
		// Arrange
		final var name = "someName";
		final var fileName = "someFileName";
		final var mimeType = "someContentType";
		final var content = "someContent";

		// Act
		final var result = new Document(name, fileName, mimeType, content);

		// Assert
		assertThat(result.name()).isEqualTo(name);
		assertThat(result.fileName()).isEqualTo(fileName);
		assertThat(result.mimeType()).isEqualTo(mimeType);
		assertThat(result.content()).isEqualTo(content);
	}

}
