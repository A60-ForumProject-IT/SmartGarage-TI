package com.telerikacademy.web.smartgarageti.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    private String name;
    private String email;
    private String phone;
    private String vehicleYear;
    private String vehicleMake;
    private String vehicleMileage;
    private String appointmentDate;
    private String timeFrame;
    private String[] services;
    private String message;
}
