package ru.yandex.practicum.filmorate.config.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {
    int day;
    int month;
    int year;

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
