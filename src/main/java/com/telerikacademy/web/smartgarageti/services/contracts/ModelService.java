package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Model;

public interface ModelService {
    Model findModelByName(String name);
}
