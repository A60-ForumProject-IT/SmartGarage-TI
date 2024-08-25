package com.telerikacademy.web.smartgarageti.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
public class FilteredUserOptions {

    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<String> vehicleBrand;
    private Optional<LocalDate> visitDateFrom;
    private Optional<LocalDate> visitDateTo;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public FilteredUserOptions(String username,
                               String email,
                               String phoneNumber,
                               String vehicleBrand,
                               LocalDate visitDateFrom,
                               LocalDate visitDateTo,
                               String sortBy,
                               String sortOrder) {
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.phoneNumber = Optional.ofNullable(phoneNumber);
        this.vehicleBrand = Optional.ofNullable(vehicleBrand);
        this.visitDateFrom = Optional.ofNullable(visitDateFrom);
        this.visitDateTo = Optional.ofNullable(visitDateTo);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }
}
