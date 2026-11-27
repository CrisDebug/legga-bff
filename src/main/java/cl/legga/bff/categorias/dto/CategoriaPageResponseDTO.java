package cl.legga.bff.categorias.dto;

import java.util.List;

public record CategoriaPageResponseDTO(
    List<CategoriaResponseDTO> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
}