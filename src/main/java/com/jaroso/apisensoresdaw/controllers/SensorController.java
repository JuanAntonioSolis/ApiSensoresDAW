package com.jaroso.apisensoresdaw.controllers;

import com.jaroso.apisensoresdaw.dtos.SensorCreateDto;
import com.jaroso.apisensoresdaw.dtos.SensorDto;
import com.jaroso.apisensoresdaw.dtos.SensorUpdateDto;
import com.jaroso.apisensoresdaw.entities.Sensor;
import com.jaroso.apisensoresdaw.mappers.SensorMapper;
import com.jaroso.apisensoresdaw.repositories.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private SensorMapper sensorMapper;

    @GetMapping
    public ResponseEntity<List<SensorDto>> findAll() {
       return ResponseEntity.ok(sensorRepository.findAll().stream()
               .map(sensorMapper::sensorToDto)
               .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDto> findById(@PathVariable Long id) {
        Optional<SensorDto>sensorDto=sensorRepository.findById(id).map(sensorMapper::sensorToDto);
        return sensorDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SensorDto> saveSensor(@RequestBody SensorCreateDto sensor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorMapper.sensorToDto(sensorRepository.save(sensorMapper.toEntity(sensor))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SensorDto> deleteSensor(@PathVariable Long id) {
        Optional<SensorDto>sensor=sensorRepository.findById(id).map(sensorMapper::sensorToDto);
        if (sensor.isPresent()) {
            sensorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorDto> updateSensor(@PathVariable Long id, @RequestBody SensorUpdateDto sensorUpdateDto) {
        Optional<Sensor> sensor = sensorRepository.findById(id);
        if (sensor.isPresent()) {
            sensor.get().setState(sensorUpdateDto.state());
            return ResponseEntity.ok(sensorMapper.sensorToDto(sensorRepository.save(sensor.get())));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}