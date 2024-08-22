package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.User;

public interface UserRepository {
    User getByUsername(String username);

    User getUserById(int id);

}
