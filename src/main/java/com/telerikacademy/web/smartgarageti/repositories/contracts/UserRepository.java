package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.FilteredUserOptions;
import com.telerikacademy.web.smartgarageti.models.User;

import java.util.List;

public interface UserRepository {
    User getByUsername(String username);

    User getUserByEmail(String email);

    User getUserById(int id);

    void create(User user);

    void update(User user);

    List<User> getAllUsers(FilteredUserOptions filteredUserOptions, int page, int size);

}
