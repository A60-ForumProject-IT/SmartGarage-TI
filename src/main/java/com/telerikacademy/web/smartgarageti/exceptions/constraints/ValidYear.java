package com.telerikacademy.web.smartgarageti.exceptions.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidYearValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidYear {
    String message() default "The year must be between 1886 and the current year.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
