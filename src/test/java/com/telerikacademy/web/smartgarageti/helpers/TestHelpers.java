package com.telerikacademy.web.smartgarageti.helpers;

import com.telerikacademy.web.smartgarageti.models.*;

import java.time.LocalDate;

public class TestHelpers {

    public User createMockUser() {
        User mockUser = new User();
        mockUser.setUsername("MockUsername");
        mockUser.setFirstName("MockFirstName");
        mockUser.setLastName("MockLastName");
        mockUser.setEmail("mockEmail@test.com");
        mockUser.setPassword("mockPassword%");
        mockUser.setPhoneNumber("0895999999");
        mockUser.setRole(createMockRoleUser());
        return mockUser;
    }

    public User createMockUserEmployee() {
        User mockUser = new User();
        mockUser.setUsername("MockUsername");
        mockUser.setFirstName("MockFirstName");
        mockUser.setLastName("MockLastName");
        mockUser.setEmail("mockEmail@test.com");
        mockUser.setPassword("mockPassword%");
        mockUser.setPhoneNumber("0895999999");
        mockUser.setRole(createMockRoleEmployee());
        return mockUser;
    }

    public Role createMockRoleEmployee() {
        Role mockRole = new Role();
        mockRole.setId(2);
        mockRole.setName("MockRoleEmployee");
        return mockRole;
    }

    public Role createMockRoleUser() {
        Role mockRole = new Role();
        mockRole.setId(1);
        mockRole.setName("MockRoleUser");
        return mockRole;
    }

    public Brand createMockBrand() {
        Brand mockBrand = new Brand();
        mockBrand.setName("MockBrand");
        mockBrand.setId(1);
        return mockBrand;
    }

    public EngineType createMockEngineType() {
        EngineType mockEngineType = new EngineType();
        mockEngineType.setName("MockEngineType");
        mockEngineType.setId(1);
        return mockEngineType;
    }

    public Model createMockModel() {
        Model mockModel = new Model();
        mockModel.setName("MockModel");
        mockModel.setId(1);
        return mockModel;
    }

    public Year createMockYear() {
        Year mockYear = new Year();
        mockYear.setYear(2000);
        mockYear.setId(1);
        return mockYear;
    }

    public Vehicle createMockVehicle() {
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setBrand(createMockBrand());
        mockVehicle.setModel(createMockModel());
        mockVehicle.setYear(createMockYear());
        mockVehicle.setEngineType(createMockEngineType());
        return mockVehicle;
    }

    public RepairService createMockRepairService() {
        RepairService mockRepairService = new RepairService();
        mockRepairService.setName("MockRepairService");
        mockRepairService.setId(1);
        return mockRepairService;
    }

    public ClientCar createMockClientCar() {
        ClientCar mockClientCar = new ClientCar();
        mockClientCar.setId(1);
        mockClientCar.setVin("X1X1X1X1X1X1X1X1X");
        mockClientCar.setLicensePlate("CH6666AP");
        mockClientCar.setOwner(createMockUser());
        mockClientCar.setVehicle(createMockVehicle());
        return mockClientCar;
    }

    public Order createMockOrder() {
        Order mockOrder = new Order();
        mockOrder.setId(1);
        mockOrder.setClientCar(createMockClientCar());
        mockOrder.setStatus("NOT_STARTED");
        return mockOrder;
    }

    public CarServiceLog createMockCarServiceLog() {
        CarServiceLog mockCarServiceLog = new CarServiceLog();
        mockCarServiceLog.setId(1);
        mockCarServiceLog.setClientCar(createMockClientCar());
        mockCarServiceLog.setService(createMockRepairService());
        mockCarServiceLog.setServiceDate(LocalDate.of(2000, 1, 30));
        mockCarServiceLog.setOrder(createMockOrder());
        mockCarServiceLog.setCalculatedPrice(99.99);
        return mockCarServiceLog;
    }

    public FilteredUserOptions createMockFilteredUserOptions() {
        return new FilteredUserOptions(
                "MockUsername",
                "mockEmail@test.com",
                "0898999999",
                "MockBrand",
                LocalDate.of(2000, 2, 25),
                LocalDate.of(2000, 10, 25),
                null,
                null
        );
    }
}
