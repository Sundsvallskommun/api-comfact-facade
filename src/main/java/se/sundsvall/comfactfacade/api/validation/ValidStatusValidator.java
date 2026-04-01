package se.sundsvall.comfactfacade.api.validation;

import generated.se.sundsvall.comfact.StatusPatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidStatusValidator implements ConstraintValidator<ValidStatus, String> {

	private static final Set<String> VALID_VALUES = Arrays.stream(StatusPatch.values())
		.map(StatusPatch::getValue)
		.map(String::toLowerCase)
		.collect(Collectors.toSet());

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null || VALID_VALUES.contains(value.toLowerCase())) {
			return true;
		}
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(
			"Invalid status: '" + value + "'. Valid values are: " + String.join(", ", VALID_VALUES))
			.addConstraintViolation();
		return false;
	}
}
