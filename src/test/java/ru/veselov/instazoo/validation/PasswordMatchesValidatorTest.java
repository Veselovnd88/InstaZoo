package ru.veselov.instazoo.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.instazoo.payload.request.SignUpRequest;

@ExtendWith(MockitoExtension.class)
class PasswordMatchesValidatorTest {
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    PasswordMatchesValidator passwordMatchesValidator;

    @BeforeEach
    void init() {
        passwordMatchesValidator = new PasswordMatchesValidator();
    }

    @Test
    void shouldReturnTrue() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setPassword("blala");
        signUpRequest.setConfirmPassword("blala");

        boolean valid = passwordMatchesValidator.isValid(signUpRequest, constraintValidatorContext);

        Assertions.assertThat(valid).isTrue();
    }

    @Test
    void shouldReturnFalse() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setPassword("blala");
        signUpRequest.setConfirmPassword("noBlala");

        boolean valid = passwordMatchesValidator.isValid(signUpRequest, constraintValidatorContext);

        Assertions.assertThat(valid).isFalse();
    }

}