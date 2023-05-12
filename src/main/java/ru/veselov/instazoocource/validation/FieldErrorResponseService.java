package ru.veselov.instazoocource.validation;

import org.springframework.validation.BindingResult;

public interface FieldErrorResponseService {

    void validateFields(BindingResult result);

}
