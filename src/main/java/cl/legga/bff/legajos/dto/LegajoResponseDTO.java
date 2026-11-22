
package cl.legga.bff.legajos.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de respuesta de un legajo.
 *
 * Representa el contrato de datos que recibe el BFF
 * desde MS-Legajos y que posteriormente entrega al frontend.
 *
 * Incluye los datos de la categoría para permitir que Angular
 * visualice su identificador y nombre tanto en el listado
 * como en el detalle del legajo.
 */
public record LegajoResponseDTO(

        // Identificador único del legajo.
        UUID id,

        // Información principal.
        String titulo,
        String descripcion,

        // Información de la categoría asociada.
        UUID categoriaId,
        String categoriaNombre,

        // Estado actual del legajo.
        String estado,

        // Fechas de creación y última actualización.
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt

) {
}
