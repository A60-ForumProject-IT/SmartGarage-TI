package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.models.User;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface RepairServiceService {
    RepairService findServiceByName(String brandName);

    List<RepairService> findAllServices();

    RepairService findServiceById(int id);

    RepairService createService(RepairService service, User user);

    void deleteService(int id, User user);

    List<RepairService> filterServices(String name, Double price, Sort sort, User user);

}
