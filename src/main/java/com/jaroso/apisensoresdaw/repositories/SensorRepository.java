package com.jaroso.apisensoresdaw.repositories;

import com.jaroso.apisensoresdaw.dtos.SensorDto;
import com.jaroso.apisensoresdaw.entities.Sensor;
import com.jaroso.apisensoresdaw.enums.SensorTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

}