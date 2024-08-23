package com.telerikacademy.web.smartgarageti.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "clients_cars")
@NoArgsConstructor
public class ClientCar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "vin", nullable = false, unique = true)
    private String vin;

    @Column(name = "plate", nullable = false, unique = true)
    private String licensePlate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @JsonIgnore
    @OneToMany(mappedBy = "clientCar", fetch = FetchType.EAGER)
    private List<CarService> carServices;

    public ClientCar(String vin, String licensePlate, User owner, Vehicle vehicle) {
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.owner = owner;
        this.vehicle = vehicle;
    }

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