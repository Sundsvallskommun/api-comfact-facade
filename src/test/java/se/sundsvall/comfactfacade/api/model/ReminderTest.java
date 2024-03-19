package se.sundsvall.comfactfacade.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

import java.time.OffsetDateTime;

import com.google.code.beanmatchers.BeanMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReminderTest {

	@BeforeAll
	static void setUp() {
		BeanMatchers.registerValueGenerator(OffsetDateTime::now, OffsetDateTime.class);
	}

	@Test
	void bean() {
		MatcherAssert.assertThat(Reminder.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {

		// Arrange
		final var enabled = true;
		final var intervalInHours = 1;
		final var message = NotificationMessage.builder().build();
		final var startDateTime = OffsetDateTime.now();

		// Act
		final var result = Reminder.builder()
			.withEnabled(enabled)
			.withIntervalInHours(intervalInHours)
			.withMessage(message)
			.withStartDateTime(startDateTime)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.isEnabled()).isEqualTo(enabled);
		assertThat(result.getIntervalInHours()).isEqualTo(intervalInHours);
		assertThat(result.getMessage()).isEqualTo(message);
		assertThat(result.getStartDateTime()).isEqualTo(startDateTime);

	}


	@Test
	void noDirtOnCreatedBean() {
		assertThat(Reminder.builder().build()).hasAllNullFieldsOrPropertiesExcept("enabled", "intervalInHours");
		assertThat(new Reminder()).hasAllNullFieldsOrPropertiesExcept("enabled", "intervalInHours");
	}

}
