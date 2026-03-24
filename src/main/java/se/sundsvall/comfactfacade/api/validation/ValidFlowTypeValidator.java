package se.sundsvall.comfactfacade.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class ValidFlowTypeValidator implements ConstraintValidator<ValidFlowType, String> {

	private static final Set<String> VALID_VALUES = Set.of("Parallel", "Sequential");

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		return value == null || VALID_VALUES.contains(value);
	}
}
