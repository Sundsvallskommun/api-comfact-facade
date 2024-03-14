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

class IdentificationTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Identification.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}


	@Test
	void testConstructorAndGetters() {

		// Arrange
		final String alias = "test";

		// Act
		final Identification testObject = new Identification(alias);

		// Assert
		assertThat(testObject.getAlias()).isEqualTo(alias);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Identification.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Identification()).hasAllNullFieldsOrProperties();
	}

}
