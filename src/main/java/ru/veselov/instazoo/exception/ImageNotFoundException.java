package ru.veselov.instazoo.exception;

import jakarta.persistence.EntityNotFoundException;
import org.w3c.dom.Entity;

public class ImageNotFoundException extends EntityNotFoundException {

    public ImageNotFoundException(String message) {
        super(message);
    }
}
