package se.sundsvall.comfactfacade.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidStatusValidator.class)
@Target({
	ElementType.FIELD, ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStatus {

	// Will be replaced by the actual message produced by ValidStatusValidator.
	String message() default "Invalid status. Valid values are: active, withdrawn";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
