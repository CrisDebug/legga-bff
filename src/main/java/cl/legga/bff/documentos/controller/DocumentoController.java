package cl.legga.bff.documentos.controller;

import cl.legga.bff.documentos.client.DocumentoClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documentos")
public class DocumentoController {

    private final DocumentoClient documentoClient;

    public DocumentoController(DocumentoClient documentoClient) {
        this.documentoClient = documentoClient;
    }

    @GetMapping("/legajo/{legajoId}")
    public List<Map<String, Object>> buscarPorLegajo(
            @PathVariable UUID legajoId) {

        return documentoClient.buscarPorLegajo(legajoId);
    }

    @GetMapping("/{id}")
    public Map<String, Object> buscarPorId(@PathVariable UUID id) {

        return documentoClient.buscarPorId(id);
    }

    // enlace para descarga de documentos
    @GetMapping("/{id}/descarga")
    public Map<String, Object> generarEnlaceDescarga(@PathVariable UUID id) {
        return documentoClient.generarEnlaceDescarga(id);
    }
}