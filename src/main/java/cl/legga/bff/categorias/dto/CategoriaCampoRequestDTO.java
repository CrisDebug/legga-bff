package cl.legga.bff.categorias.dto;

public record CategoriaCampoRequestDTO(
        String codigo,
        String etiqueta,
        String tipoDato,
        Boolean obligatorio,
        Integer orden,
        Object opciones) {
}