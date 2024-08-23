package com.telerikacademy.web.smartgarageti.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "cars_services")
@NoArgsConstructor
public class CarService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "clients_cars_id", nullable = false)
    private ClientCar clientCar;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private RepairService service;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "calculated_price")
    private double calculatedPrice;

    public CarService(RepairService service, ClientCar clientCar) {
        this.service = service;
        this.clientCar = clientCar;
        this.serviceDate = LocalDate.now();
        this.calculatedPrice = service.getPriceBasedOnYear(clientCar.getVehicle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarService that = (CarService) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}