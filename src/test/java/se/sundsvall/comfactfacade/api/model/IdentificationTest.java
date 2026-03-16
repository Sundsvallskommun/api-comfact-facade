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

class IdentificationTest {

	@Test
	void bean() {
		MatcherAssert.assertThat(Identification.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {

		// Arrange
		final var alias = "test";

		// Act
		final var result = Identification.builder()
			.withAlias(alias)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getAlias()).isEqualTo(alias);

	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(Identification.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Identification()).hasAllNullFieldsOrProperties();
	}

}
