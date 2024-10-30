package se.sundsvall.comfactfacade.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

class SigningsResponseTest {

	@Test
	void bean() {
		MatcherAssert.assertThat(SigningsResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void constructor() {
		// Arrange
		final var pagingAndSortingMetaData = new PagingAndSortingMetaData();
		final var signingInstances = List.of(new SigningInstance());

		// Act
		final var signingsResponse = SigningsResponse.builder()
			.withPagingAndSortingMetaData(pagingAndSortingMetaData)
			.withSigningInstances(signingInstances)
			.build();

		// Assert
		assertThat(signingsResponse).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(signingsResponse.getPagingAndSortingMetaData()).isSameAs(pagingAndSortingMetaData);
		assertThat(signingsResponse.getSigningInstances()).isSameAs(signingInstances);
	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(SigningsResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SigningsResponse()).hasAllNullFieldsOrProperties();
	}

}
