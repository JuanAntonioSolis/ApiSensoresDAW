package com.jaroso.apisensoresdaw.mappers;

import com.jaroso.apisensoresdaw.dtos.SensorCreateDto;
import com.jaroso.apisensoresdaw.dtos.SensorDto;
import com.jaroso.apisensoresdaw.dtos.SensorUpdateDto;
import com.jaroso.apisensoresdaw.entities.Sensor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SensorMapper {

    SensorDto sensorToDto(Sensor sensor);
    Sensor toEntity(SensorCreateDto sensorDto);
    Sensor updateToEntity(SensorUpdateDto sensorDto);

}