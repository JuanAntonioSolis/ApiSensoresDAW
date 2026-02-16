package com.jaroso.apisensoresdaw.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "lectures")
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Double valor;

    private String unidad; //‘L/min’, ‘bar’, ‘%’, ‘°C’, etc.

    private LocalDateTime timeDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensorId")
    private Sensor sensor;

}
