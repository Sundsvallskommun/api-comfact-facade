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

class StatusTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Status.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testConstructorAndGetters() {
		// Arrange
		final var code = "someCode";
		final var message = "someMessage";

		// Act
		final var result = Status.builder()
			.withCode(code)
			.withMessage(message)
			.build();

		// Assert
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getCode()).isEqualTo(code);
		assertThat(result.getMessage()).isEqualTo(message);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Status.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Status()).hasAllNullFieldsOrProperties();
	}

}
