package cl.legga.bff.categorias.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoriaCampoResponseDTO(
        UUID id,
        UUID categoriaId,
        String codigo,
        String etiqueta,
        String tipoDato,
        Boolean obligatorio,
        Integer orden,
        Boolean activo,
        Object opciones,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}