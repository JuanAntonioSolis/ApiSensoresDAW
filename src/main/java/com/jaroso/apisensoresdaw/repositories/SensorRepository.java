package com.jaroso.apisensoresdaw.repositories;

import com.jaroso.apisensoresdaw.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

}