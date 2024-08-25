package com.telerikacademy.web.smartgarageti.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String type, int id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found.", type, attribute, value));
    }

    public EntityNotFoundException(String attribute) {
        super(String.format("This %s does not exist!", attribute));
    }


    public EntityNotFoundException() {

    }
}
