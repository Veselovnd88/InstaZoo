package ru.veselov.instazoo.validation;

import org.springframework.validation.BindingResult;

public interface FieldErrorResponseService {

    void validateFields(BindingResult result);

}