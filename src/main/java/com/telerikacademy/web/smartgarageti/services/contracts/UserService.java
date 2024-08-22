package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;

public interface UserService {
    User getByUsername(String username);

    User getUserById(User employee, int id);

    UserDto createCustomerProfile(UserCreationDto userCreationDto);
}
