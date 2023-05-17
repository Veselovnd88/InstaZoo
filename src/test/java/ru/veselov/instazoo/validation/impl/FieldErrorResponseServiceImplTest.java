package ru.veselov.instazoo.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import ru.veselov.instazoo.exception.CustomValidationException;

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
                        new FieldError("FieldError", "fieldOne", "messageOne"),
                        new FieldError("FieldError", "fieldTwo", "messageTwo")
                )
        );
        String[] codes = new String[]{"Error"};
        when(bindingResult.getAllErrors()).thenReturn(List.of(
                new ObjectError("Name", codes, null, "message3"),
                new FieldError("FieldError", "fieldOne", "messageOne"),
                new FieldError("FieldError", "fieldTwo", "messageTwo")
        ));
        Assertions.assertThatThrownBy(() ->
                        fieldErrorResponseService.validateFields(bindingResult))
                .isInstanceOf(CustomValidationException.class).hasMessageStartingWith("Wrong fields")
                .hasFieldOrPropertyWithValue("validationMap", Map.of(
                                "fieldOne", "messageOne",
                                "fieldTwo", "messageTwo",
                                "Error", "message3"
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