package cl.legga.bff.categorias.dto;

public record CategoriaDocumentoRequeridoCreateRequestDTO(
    String codigo,
    String nombre,
    String descripcion,
    Boolean obligatorio,
    Integer orden
) {
}