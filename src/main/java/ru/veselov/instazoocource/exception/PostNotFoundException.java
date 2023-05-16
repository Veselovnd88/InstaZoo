package ru.veselov.instazoocource.exception;

import jakarta.persistence.EntityNotFoundException;

public class PostNotFoundException extends EntityNotFoundException {
    public PostNotFoundException(String message) {
        super(message);
    }

}