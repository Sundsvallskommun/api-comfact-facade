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
		final NotificationMessage testObject = NotificationMessage.builder()
			.withSubject(subject)
			.withBody(body)
			.withLanguage(language)
			.build();

		// Assert
		assertThat(testObject.getSubject()).isEqualTo(subject);
		assertThat(testObject.getBody()).isEqualTo(body);
		assertThat(testObject.getLanguage()).isEqualTo(language);

	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(NotificationMessage.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new NotificationMessage()).hasAllNullFieldsOrProperties();
	}

}
