package com.jaroso.apisensoresdaw.mappers;

import com.jaroso.apisensoresdaw.dtos.LectureCreateDto;
import com.jaroso.apisensoresdaw.dtos.LectureDto;
import com.jaroso.apisensoresdaw.entities.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LectureMapper {

    //@Mappings({})
    LectureDto lectureToDto(Lecture lecture);
    Lecture toEntity(LectureCreateDto lectureDto);
}
