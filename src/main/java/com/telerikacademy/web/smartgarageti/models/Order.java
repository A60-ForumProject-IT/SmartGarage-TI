package com.telerikacademy.web.smartgarageti.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "client_car_id", nullable = false)
    private ClientCar clientCar;

    @Column(name = "status", nullable = false)
    private String status;


    public enum OrderStatus {
        NOT_STARTED,
        IN_PROGRESS,
        READY_FOR_PICKUP
    }
}
