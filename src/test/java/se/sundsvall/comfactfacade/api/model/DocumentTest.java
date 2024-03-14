package se.sundsvall.comfactfacade.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class DocumentTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Document.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

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
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getFileName()).isEqualTo(fileName);
		assertThat(result.getMimeType()).isEqualTo(mimeType);
		assertThat(result.getContent()).isEqualTo(content);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Document.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Document()).hasAllNullFieldsOrProperties();
	}

}
