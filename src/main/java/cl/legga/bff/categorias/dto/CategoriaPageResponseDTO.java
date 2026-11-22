package cl.legga.bff.categorias.dto;

import java.util.List;

public record CategoriaPageResponseDTO(
    List<CategoriaResponseDTO> content,
    int totalPages,
    long totalElements,
    int number,
    int size
) {}
