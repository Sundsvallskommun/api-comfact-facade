package se.sundsvall.comfactfacade.api.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidFlowTypeValidatorTest {

	private final ValidFlowTypeValidator validator = new ValidFlowTypeValidator();

	@Test
	void nullIsValid() {
		assertThat(validator.isValid(null, null)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"Parallel", "Sequential"
	})
	void validValues(final String value) {
		assertThat(validator.isValid(value, null)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"parallel", "sequential", "PARALLEL", "SEQUENTIAL", "invalid", ""
	})
	void invalidValues(final String value) {
		assertThat(validator.isValid(value, null)).isFalse();
	}
}
