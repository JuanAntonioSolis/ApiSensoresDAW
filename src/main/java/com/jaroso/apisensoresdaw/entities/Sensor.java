package com.jaroso.apisensoresdaw.entities;

import com.jaroso.apisensoresdaw.enums.SensorEstado;
import com.jaroso.apisensoresdaw.enums.SensorTipo;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "sensores")
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String name;
    private String description;
    private String sector;  //VOLUMEN,CAUDAL,PRESION,HUMEDAD

   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
    private SensorTipo type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorEstado state; // //ACTIVO = 1, INACTIVO = 0, MANTENIMIENTO = 2, ABIERTO = 3, CERRADO = 4, APAGADO = 5, ENCENDIDO = 6

}
