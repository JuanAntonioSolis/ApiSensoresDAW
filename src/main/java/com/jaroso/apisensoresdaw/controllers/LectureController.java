package com.jaroso.apisensoresdaw.controllers;

import com.jaroso.apisensoresdaw.dtos.LectureCreateDto;
import com.jaroso.apisensoresdaw.dtos.LectureDto;
import com.jaroso.apisensoresdaw.entities.Lecture;
import com.jaroso.apisensoresdaw.entities.Sensor;
import com.jaroso.apisensoresdaw.mappers.LectureMapper;
import com.jaroso.apisensoresdaw.repositories.LectureRepository;
import com.jaroso.apisensoresdaw.repositories.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lectures")
public class LectureController {

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private LectureMapper mapper;

    @GetMapping
    public ResponseEntity<List<LectureDto>> findAll() {
        return ResponseEntity.ok(lectureRepository.findAll().stream()
                .map(mapper::lectureToDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LectureDto> findById(@PathVariable Long id) {
        Optional<LectureDto> lecture = lectureRepository.findById(id).map(mapper::lectureToDto);

        return lecture.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<LectureDto> saveLecture (@RequestBody LectureCreateDto lectureCreateDto) {

        //El que busca en BBDD
        Optional<Sensor> sensor = sensorRepository.findById(lectureCreateDto.sensorId());

        if (sensor.isPresent()) {
            Lecture lecture1 = mapper.toEntity(lectureCreateDto);
            lecture1.setSensor(sensor.get());
            lecture1.setTimeDay(LocalDateTime.now());
            lectureRepository.save(lecture1);

            return ResponseEntity.ok(mapper.lectureToDto(lecture1));
        } else{
            return ResponseEntity.notFound().build();
        }
    }


}
