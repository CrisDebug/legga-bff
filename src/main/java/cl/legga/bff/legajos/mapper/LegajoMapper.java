package cl.legga.bff.legajos.mapper;

import cl.legga.bff.legajos.dto.LegajoCreateMSRequest;
import cl.legga.bff.legajos.dto.LegajoCreateRequest;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LegajoMapper {

    LegajoCreateMSRequest toMSRequest(LegajoCreateRequest request);
}