package se.sundsvall.comfactfacade.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class CreateSigningResponseTest {

	@Test
	void bean() {
		MatcherAssert.assertThat(CreateSigningResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var signingId = "signingId";
		final var signatoryUrls = Map.of("somePartyId", "someUrl");
		// Act
		final var result = CreateSigningResponse.builder()
			.withSigningId(signingId)
			.withSignatoryUrls(signatoryUrls)
			.build();
		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getSigningId()).isEqualTo(signingId);
		assertThat(result.getSignatoryUrls()).isEqualTo(signatoryUrls);

	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(CreateSigningResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new CreateSigningResponse()).hasAllNullFieldsOrProperties();
	}

}
