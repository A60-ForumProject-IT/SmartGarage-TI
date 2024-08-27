package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.helpers.TestHelpers;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ClientCarRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ClientCarServiceImplTests {

    @Mock
    private ClientCarRepository clientCarRepository;

    @InjectMocks
    private ClientCarServiceImpl carService;

    @Test
    public void createClientCar_ShouldThrow_IfVinExists() {
        ClientCar carToBeCreated = TestHelpers.createMockClientCar();

        Mockito.when(clientCarRepository.findByVin(carToBeCreated.getVin()))
                .thenReturn(Optional.of(carToBeCreated));

        Assertions.assertThrows(DuplicateEntityException.class, () -> carService.createClientCar(carToBeCreated));
    }
}
