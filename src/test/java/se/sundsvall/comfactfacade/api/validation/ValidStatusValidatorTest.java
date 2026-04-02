package se.sundsvall.comfactfacade.api.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidStatusValidatorTest {

	@Mock
	private ConstraintValidatorContext contextMock;

	@Mock
	private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilderMock;

	private final ValidStatusValidator validator = new ValidStatusValidator();

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(contextMock, violationBuilderMock);
	}

	@Test
	void nullIsValid() {
		assertThat(validator.isValid(null, contextMock)).isTrue();

		verifyNoInteractions(contextMock);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"active", "Active", "ACTIVE", "withdrawn", "Withdrawn", "WITHDRAWN"
	})
	void validValues(final String value) {
		assertThat(validator.isValid(value, contextMock)).isTrue();

		verifyNoInteractions(contextMock);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"invalid", "", " "
	})
	void invalidValues(final String value) {
		when(contextMock.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilderMock);

		assertThat(validator.isValid(value, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate(anyString());
		verify(violationBuilderMock).addConstraintViolation();
	}
}
