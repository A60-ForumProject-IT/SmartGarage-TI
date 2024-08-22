package com.telerikacademy.web.smartgarageti.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @ManyToOne
    @JoinColumn(name = "year_id", nullable = false)
    private Year year;
}