package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.BaseService;

import java.util.List;

public interface BaseServiceService {
    List<BaseService> getAllBaseServices();

    BaseService getBaseServiceById(int id);

    BaseService findByName(String name);
}
