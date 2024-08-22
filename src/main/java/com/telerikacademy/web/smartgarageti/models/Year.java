package com.telerikacademy.web.smartgarageti.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "years")
@NoArgsConstructor
public class Year {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "year")
    private int year;

    public Year(int year) {
        this.year = year;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Year year = (Year) o;
        return Objects.equals(id, year.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}