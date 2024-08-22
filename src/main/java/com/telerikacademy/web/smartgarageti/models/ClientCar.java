package com.telerikacademy.web.smartgarageti.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "clients_cars")
public class ClientCar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "vin", nullable = false, unique = true)
    private String vin;

    @Column(name = "plate", nullable = false, unique = true)
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @OneToMany(mappedBy = "clientCar", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarService> carServices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientCar clientCar = (ClientCar) o;
        return id == clientCar.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}