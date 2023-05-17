package ru.veselov.instazoo.exception;

import jakarta.persistence.EntityNotFoundException;

public class PostNotFoundException extends EntityNotFoundException {
    public PostNotFoundException(String message) {
        super(message);
    }

}