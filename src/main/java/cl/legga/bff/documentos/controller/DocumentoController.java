
package cl.legga.bff.documentos.controller;

import cl.legga.bff.documentos.client.DocumentoClient;
import cl.legga.bff.identidad.model.EasyAuthIdentity;
import cl.legga.bff.identidad.resolver.EasyAuthPrincipalResolver;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documentos")
public class DocumentoController {

    private final DocumentoClient documentoClient;
    private final EasyAuthPrincipalResolver identityResolver;

    public DocumentoController(
            DocumentoClient documentoClient,
            EasyAuthPrincipalResolver identityResolver) {

        this.documentoClient = documentoClient;
        this.identityResolver = identityResolver;
    }

    // =========================================================
    // CARGAR DOCUMENTO
    // =========================================================

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> cargarDocumento(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("legajoId") UUID legajoId,
            @RequestParam("requisitoId") UUID requisitoId,
            HttpServletRequest request) throws IOException {

        EasyAuthIdentity identity = obtenerIdentidad(request);

        String uploadedBy = identity.email();

        if (uploadedBy == null || uploadedBy.isBlank()) {
            uploadedBy = identity.objectId();
        }

        return documentoClient.cargarDocumento(
                archivo,
                legajoId,
                requisitoId,
                uploadedBy,
                identity);
    }

    // =========================================================
    // CONSULTAR DOCUMENTOS DE UN LEGAJO
    // =========================================================

    @GetMapping("/legajo/{legajoId}")
    public List<Map<String, Object>> buscarPorLegajo(
            @PathVariable UUID legajoId) {

        return documentoClient.buscarPorLegajo(legajoId);
    }

    // =========================================================
    // CONSULTAR DOCUMENTO POR ID
    // =========================================================

    @GetMapping("/{id}")
    public Map<String, Object> buscarPorId(
            @PathVariable UUID id) {

        return documentoClient.buscarPorId(id);
    }

    // =========================================================
    // GENERAR ENLACE DE DESCARGA
    // =========================================================

    @GetMapping("/{id}/descarga")
    public Map<String, Object> generarEnlaceDescarga(
            @PathVariable UUID id) {

        return documentoClient.generarEnlaceDescarga(id);
    }

    // =========================================================
    // RESOLVER IDENTIDAD
    // =========================================================

    private EasyAuthIdentity obtenerIdentidad(
            HttpServletRequest request) {

        return identityResolver.resolve(request)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "No se pudo obtener la identidad autenticada"));
    }
    // =========================================================
    // CONSULTAR DOCUMENTOS DE UN REQUISITO
    // =========================================================

    @GetMapping("/requisito/{requisitoId}")
    public List<Map<String, Object>> buscarPorRequisito(
            @PathVariable UUID requisitoId) {

        return documentoClient.buscarPorRequisito(requisitoId);
    }
}
