package ru.veselov.instazoocource.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.veselov.instazoocource.exception.CustomValidationException;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldErrorResponseServiceImplTest {

    @Mock
    BindingResult bindingResult;

    FieldErrorResponseServiceImpl fieldErrorResponseService;

    @BeforeEach
    void init() {
        fieldErrorResponseService = new FieldErrorResponseServiceImpl();
    }

    @Test
    void shouldThrowException() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                        new FieldError("Object", "fieldOne", "messageOne"),
                        new FieldError("Object", "fieldTwo", "messageTwo")
                )
        );
        Assertions.assertThatThrownBy(() ->
                        fieldErrorResponseService.validateFields(bindingResult))
                .isInstanceOf(CustomValidationException.class).hasMessageStartingWith("Wrong fields")
                .hasFieldOrPropertyWithValue("validationMap", Map.of(
                                "fieldOne", "messageOne",
                                "fieldTwo", "messageTwo"
                        )
                );
    }

    @Test
    void shouldValidate() {
        when(bindingResult.hasErrors()).thenReturn(false);
        Assertions.assertThatNoException().isThrownBy(() ->
                fieldErrorResponseService.validateFields(bindingResult)
        );
    }

}