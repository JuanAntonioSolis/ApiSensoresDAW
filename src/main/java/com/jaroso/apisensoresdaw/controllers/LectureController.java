package com.jaroso.apisensoresdaw.controllers;

import com.jaroso.apisensoresdaw.dtos.DateRangeDto;
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

    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<List<LectureDto>> findAllByDatesBetween(@PathVariable Long sensorId,
                                                                  @RequestBody DateRangeDto dateRange) {

        List<Lecture> allLectures = lectureRepository.findBySensorId(sensorId);

        if (allLectures.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LocalDateTime earliestDate = allLectures.stream()
                .map(Lecture::getTimeDay)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestDate = allLectures.stream()
                .map(Lecture::getTimeDay)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (dateRange.inicio().isBefore(earliestDate) && dateRange.fin().isBefore(latestDate)
        ) {
            return ResponseEntity.notFound().build();
        } else if (dateRange.inicio().isAfter(latestDate) ) {
            return ResponseEntity.notFound().build();
        } else if (dateRange.fin().isBefore(earliestDate)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lectureRepository.findBySensorIdAndTimeDayBetween(sensorId,
                        dateRange.inicio(), dateRange.fin()).stream()
                .map(mapper::lectureToDto)
                .toList());



        /*
        // Obtener todas las lecturas del sensor
        List<Lecture> allLectures = lectureRepository.findBySensorId(sensorId);

        // Si no hay lecturas, devolver NotFound
        if (allLectures.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener la fecha más antigua y más reciente
        LocalDateTime earliestDate = allLectures.stream()
                .map(Lecture::getTimeDay)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestDate = allLectures.stream()
                .map(Lecture::getTimeDay)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // Validar que la fecha de inicio no sea anterior a la más antigua
        if (dateRange.inicio().isBefore(earliestDate)) {
            return ResponseEntity.notFound().build();
        }

        // Validar que la fecha de fin no sea posterior a la más reciente
        if (dateRange.fin().isAfter(latestDate)) {
            return ResponseEntity.notFound().build();
        }

        // Filtrar las lecturas dentro del rango de fechas
        List<LectureDto> lecturesBetween = allLectures.stream()
                .filter(lecture -> !lecture.getTimeDay().isBefore(dateRange.inicio()) &&
                        !lecture.getTimeDay().isAfter(dateRange.fin()))
                .map(mapper::lectureToDto)
                .toList();

        return ResponseEntity.ok(lecturesBetween);

         */
    }


}
