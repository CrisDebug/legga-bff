package cl.legga.bff.categorias.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoriaResponseDTO(
    UUID id,
    String nombre,
    String descripcion,
    Boolean activa,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
