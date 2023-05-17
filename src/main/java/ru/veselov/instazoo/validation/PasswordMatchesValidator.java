package ru.veselov.instazoo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.veselov.instazoo.annotations.PasswordMatches;
import ru.veselov.instazoo.payload.request.SignUpRequest;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        SignUpRequest signUpRequest = (SignUpRequest) value;
        return signUpRequest.getPassword().equals(((SignUpRequest) value).getConfirmPassword());
    }

}
