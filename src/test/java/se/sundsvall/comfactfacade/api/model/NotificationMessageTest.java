package se.sundsvall.comfactfacade.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

class NotificationMessageTest {

	@Test
	void bean() {
		MatcherAssert.assertThat(NotificationMessage.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {

		// Arrange
		final String subject = "someSubject";
		final String body = "someBody";
		final String language = "sv-SE";

		// Act
		final var result = NotificationMessage.builder()
			.withSubject(subject)
			.withBody(body)
			.withLanguage(language)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getSubject()).isEqualTo(subject);
		assertThat(result.getBody()).isEqualTo(body);
		assertThat(result.getLanguage()).isEqualTo(language);

	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(NotificationMessage.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new NotificationMessage()).hasAllNullFieldsOrProperties();
	}

}
