package com.jaroso.apisensoresdaw.controllers;

import com.jaroso.apisensoresdaw.dtos.SensorCreateDto;
import com.jaroso.apisensoresdaw.dtos.SensorDto;
import com.jaroso.apisensoresdaw.dtos.SensorUpdateDto;
import com.jaroso.apisensoresdaw.entities.Sensor;
import com.jaroso.apisensoresdaw.mappers.SensorMapper;
import com.jaroso.apisensoresdaw.repositories.SensorRepository;
import com.jaroso.apisensoresdaw.services.MqttPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private SensorMapper sensorMapper;
    @Autowired
    private MqttPublisher mqttPublisher;

    Logger logger = Logger.getLogger(LectureController.class.getName());


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
        logger.info("Actualizando sensor: " + sensorUpdateDto);
        Optional<Sensor> sensor = sensorRepository.findById(id);

        if (sensor.isPresent()) {
            sensor.get().setState(sensorUpdateDto.state());

            // publica un mensaje MQTT al topic del actuador (ej: actuadores/1/comando con payload ON o OFF)
            String payload = String.format("{\"state\": \"%s\"}", sensorUpdateDto.state());
            mqttPublisher.publish("test/sensor/bomba", payload);

            return ResponseEntity.ok(sensorMapper.sensorToDto(sensorRepository.save(sensor.get())));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}