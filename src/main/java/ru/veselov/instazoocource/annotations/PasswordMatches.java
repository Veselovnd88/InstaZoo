package ru.veselov.instazoocource.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.veselov.instazoocource.validation.PasswordMatchesValidator;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {

    String message() default "Password doesn't match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}