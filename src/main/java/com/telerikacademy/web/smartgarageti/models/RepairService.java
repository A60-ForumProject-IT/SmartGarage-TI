package com.telerikacademy.web.smartgarageti.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "services")
@NoArgsConstructor
public class RepairService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    @JsonIgnore
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarService> carServices;

    public RepairService(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPriceBasedOnYear(Vehicle vehicle) {
        int year = vehicle.getYear().getYear();

        if (year >= 1990 && year < 2000) {
            return this.price;
        } else if (year >= 2000 && year < 2010) {
            return this.price * 1.3;
        } else if (year >= 2010 && year < 2020) {
            return this.price * 1.5;
        } else if (year >= 2020) {
            return this.price * 1.7;
        }

        return this.price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairService service = (RepairService) o;
        return id == service.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}