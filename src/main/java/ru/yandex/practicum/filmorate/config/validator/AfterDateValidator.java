package ru.yandex.practicum.filmorate.config.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * Validates a date that it is in the past according to the specified parameters.
 * When creating, you must specify the day, month and year after which
 * the validated date must be.
 * Supported type {@link java.time.LocalDate}.
 *
 * @author Dmitriy Kruglov
 *
 * @see AfterDate
 */
public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {
    private int day;
    private int month;
    private int year;

    @Override
    public void initialize(AfterDate constraintAnnotation) {
        day = constraintAnnotation.day();
        month = constraintAnnotation.month();
        year = constraintAnnotation.year();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.isAfter(LocalDate.of(year, month, day));
        }
        return false;
    }
}
