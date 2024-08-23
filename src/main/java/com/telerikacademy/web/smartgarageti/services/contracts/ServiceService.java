package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.Service;

import java.util.List;

public interface ServiceService {
    Service findServiceByName(String brandName);

    List<Service> findAllServices();

    Service findServiceById(int id);

    Service createService(Service service);

    void deleteService(int id);
}
