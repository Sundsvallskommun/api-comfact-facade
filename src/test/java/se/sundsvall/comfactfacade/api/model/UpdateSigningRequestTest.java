package se.sundsvall.comfactfacade.api.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.OffsetDateTime;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

class UpdateSigningRequestTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void bean() {
		MatcherAssert.assertThat(UpdateSigningRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {
		// Arrange
		final var expires = now();
		final var status = "active";

		// Act
		final var result = UpdateSigningRequest.builder()
			.withExpires(expires)
			.withStatus(status)
			.build();

		// Assert
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getExpires()).isEqualTo(expires);
		assertThat(result.getStatus()).isEqualTo(status);
	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(UpdateSigningRequest.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new UpdateSigningRequest()).hasAllNullFieldsOrProperties();
	}
}
