
package cl.legga.bff.legajos.controller;

import cl.legga.bff.identidad.model.EasyAuthIdentity;
import cl.legga.bff.identidad.resolver.EasyAuthPrincipalResolver;
import cl.legga.bff.legajos.client.LegajoClient;
import cl.legga.bff.legajos.dto.LegajoCreateRequest;
import cl.legga.bff.legajos.dto.LegajoPageResponseDTO;
import cl.legga.bff.legajos.dto.LegajoResponseDTO;
import cl.legga.bff.legajos.mapper.LegajoMapper;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/legajos")
public class LegajoController {

    private final LegajoClient legajoClient;
    private final EasyAuthPrincipalResolver identityResolver;
    private final LegajoMapper legajoMapper;

    public LegajoController(
            LegajoClient legajoClient,
            EasyAuthPrincipalResolver identityResolver,
            LegajoMapper legajoMapper) {

        this.legajoClient = legajoClient;
        this.identityResolver = identityResolver;
        this.legajoMapper = legajoMapper;
    }

    // =========================================================
    // CONSULTA PAGINADA DE LEGAJOS
    // =========================================================

    @GetMapping
    public LegajoPageResponseDTO listar(HttpServletRequest request) {

        EasyAuthIdentity identity = obtenerIdentidad(request);

        return legajoClient.listar(identity);
    }

    // =========================================================
    // CONSULTA DE DETALLE DE LEGAJO
    // =========================================================

    @GetMapping("/{id}")
    public LegajoResponseDTO buscarPorId(
            @PathVariable UUID id,
            HttpServletRequest request) {

        EasyAuthIdentity identity = obtenerIdentidad(request);

        return legajoClient.buscarPorId(id, identity);
    }

    // =========================================================
    // CREACIÓN DE LEGAJO
    // =========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LegajoResponseDTO crear(
            @RequestBody LegajoCreateRequest request,
            HttpServletRequest httpRequest) {

        EasyAuthIdentity identity = obtenerIdentidad(httpRequest);

        // Adaptación del contrato público al contrato de MS-Legajos.
        var msRequest = legajoMapper.toMSRequest(request);

        // La categoría se envía como parámetro de consulta.
        return legajoClient.crear(
                msRequest,
                request.categoriaId(),
                identity);
    }

    // =========================================================
    // RESOLUCIÓN DE IDENTIDAD
    // =========================================================

    private EasyAuthIdentity obtenerIdentidad(HttpServletRequest request) {

        return identityResolver.resolve(request)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "No se pudo obtener la identidad autenticada"));
    }
}
