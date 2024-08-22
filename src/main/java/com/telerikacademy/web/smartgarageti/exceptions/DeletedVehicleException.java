package com.telerikacademy.web.smartgarageti.exceptions;

public class DeletedVehicleException extends RuntimeException {
    public DeletedVehicleException(String message) {
        super(message);
    }
}
