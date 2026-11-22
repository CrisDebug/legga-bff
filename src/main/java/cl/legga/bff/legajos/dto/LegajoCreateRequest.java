package cl.legga.bff.legajos.dto;

import java.util.UUID;

public record LegajoCreateRequest(
        String titulo,
        String descripcion,
        UUID categoriaId
) {}
