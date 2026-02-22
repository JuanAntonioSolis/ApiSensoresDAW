package com.jaroso.apisensoresdaw.repositories;

import com.jaroso.apisensoresdaw.entities.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    List<Lecture> findBySensorId(Long sensorId);

    List<Lecture> findBySensorIdAndTimeDayBetween(Long sensorId,
                                                  LocalDateTime inicio, LocalDateTime fin);


}
