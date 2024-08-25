package com.telerikacademy.web.smartgarageti.exceptions;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String type, String attribute, String value) {
        super(String.format("%s with %s %s already exists.", type, attribute, value));
    }

    public DuplicateEntityException(String type, int id) {
        super(String.format("%s %d already exists!", type, id));
    }

    public DuplicateEntityException(String type, String attribute) {
        super(String.format("%s with name %s already exists!", type, attribute));
    }

    public DuplicateEntityException(String type, String attribute, String secondAttribute, int year, String thirdAttribute) {
        super(String.format("%s with brand %s, model %s, year %d  and engine %s already exists!", type, attribute, secondAttribute, year, thirdAttribute));
    }

    public DuplicateEntityException(String message) {
        super(message);
    }

}
