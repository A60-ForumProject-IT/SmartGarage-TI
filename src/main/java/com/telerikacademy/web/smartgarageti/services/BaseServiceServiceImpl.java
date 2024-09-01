package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.BaseService;
import com.telerikacademy.web.smartgarageti.repositories.contracts.BaseServiceRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.BaseServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseServiceServiceImpl implements BaseServiceService {
    private final BaseServiceRepository baseServiceRepository;

    @Autowired
    public BaseServiceServiceImpl(BaseServiceRepository baseServiceRepository) {
        this.baseServiceRepository = baseServiceRepository;
    }

    @Override
    public List<BaseService> getAllBaseServices() {
        return baseServiceRepository.findAll();
    }

    @Override
    public BaseService getBaseServiceById(int id) {
        return baseServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Base Service", id));
    }
}
