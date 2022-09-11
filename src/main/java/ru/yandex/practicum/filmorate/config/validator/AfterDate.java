package ru.yandex.practicum.filmorate.config.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotated element must be a date later than the specified date.
 * Supported type {@code java.time.LocalDate}.
 * Validation and parameterization is carried out {@link AfterDateValidator}
 *
 * @author Dmitriy Kruglov
 *
 * @see java.lang.annotation.Annotation
 * @see AfterDateValidator
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterDateValidator.class)
@Documented
public @interface AfterDate {

    String message() default "{AfterDate.invalid}";

    int day() default 1;

    int month() default 1;

    int year() default 1;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
