
package cl.legga.bff.legajos.dto;

import java.util.List;

public record LegajoPageResponseDTO(
                List<LegajoResponseDTO> content,
                int page,
                int size,
                long totalElements,
                int totalPages,
                boolean first,
                boolean last) {
}
