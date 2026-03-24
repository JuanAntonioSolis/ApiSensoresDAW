package com.jaroso.apisensoresdaw.dtos;

import java.time.LocalDateTime;

public record LectureDto(Long id, Double valor, String unidad, LocalDateTime timeDay, Long sensorId) {
}
