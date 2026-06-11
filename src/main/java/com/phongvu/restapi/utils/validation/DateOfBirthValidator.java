package com.phongvu.restapi.utils.validation;

import com.phongvu.restapi.utils.annotation.DateOfBirthValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DateOfBirthValidator implements ConstraintValidator<DateOfBirthValid, LocalDate> {

    private int min;

    @Override
    public void initialize(DateOfBirthValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
//        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(value)) return true;
        long years = ChronoUnit.YEARS.between(value, LocalDate.now());
        return years > min;
    }
}
