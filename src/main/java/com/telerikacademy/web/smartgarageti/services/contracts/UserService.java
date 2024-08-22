package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.User;

public interface UserService {
    User getByUsername(String username);
}
