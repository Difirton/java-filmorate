package ru.yandex.practicum.filmorate.config.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

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
