package com.telerikacademy.web.smartgarageti.helpers;

import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.models.User;

public class PermissionHelper {
    public static final String ROLE_EMPLOYEE = "Employee";

    public static void isEmployee(User authenticatedUser, String message) {
        boolean isEmployee = false;
        if (authenticatedUser.getRole().getName().equals(ROLE_EMPLOYEE)) {
            isEmployee = true;
        }

        if (!isEmployee) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isSameUser(User user, User userToBeUpdated, String message) {
        if (!user.equals(userToBeUpdated)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isNotSameUser(User userToBeUpdated, User userIsAuthorized, String message) {

        if (userToBeUpdated.equals(userIsAuthorized)) {
            throw new UnauthorizedOperationException(message);
        }
    }
}