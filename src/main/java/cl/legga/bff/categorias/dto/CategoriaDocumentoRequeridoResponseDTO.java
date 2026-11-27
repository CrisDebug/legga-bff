package cl.legga.bff.categorias.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoriaDocumentoRequeridoResponseDTO(
    UUID id,
    UUID categoriaId,
    String codigo,
    String nombre,
    String descripcion,
    Boolean obligatorio,
    Integer orden,
    Boolean activo,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}