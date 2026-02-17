package com.jaroso.apisensoresdaw.mappers;

import com.jaroso.apisensoresdaw.dtos.LectureCreateDto;
import com.jaroso.apisensoresdaw.dtos.LectureDto;
import com.jaroso.apisensoresdaw.entities.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LectureMapper {

    @Mapping(source = "sensor.id", target = "sensorId")
    LectureDto lectureToDto(Lecture lecture);

    @Mapping(target = "sensor", ignore = true)
    Lecture toEntity(LectureCreateDto lectureDto);
}
