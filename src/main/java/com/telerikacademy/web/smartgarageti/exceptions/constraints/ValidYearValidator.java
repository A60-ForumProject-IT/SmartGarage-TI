package com.telerikacademy.web.smartgarageti.exceptions.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class ValidYearValidator implements ConstraintValidator<ValidYear, Integer> {
    private static final int MIN_YEAR = 1886;

    @Override
    public void initialize(ValidYear constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext constraintValidatorContext) {
        if (year == null) {
            return false;
        }
        int currentYear = Year.now().getValue();

        if (year < MIN_YEAR || year > currentYear) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    String.format("The year must be between %d and %d.", MIN_YEAR, currentYear)
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
