
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
    public LegajoPageResponseDTO listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            HttpServletRequest request) {

        EasyAuthIdentity identity = obtenerIdentidad(request);

        return legajoClient.listar(identity, q, categoriaId, page, size);
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
    // CREACIÃ“N DE LEGAJO
    // =========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LegajoResponseDTO crear(
            @RequestBody LegajoCreateRequest request,
            HttpServletRequest httpRequest) {

        EasyAuthIdentity identity = obtenerIdentidad(httpRequest);

        // AdaptaciÃ³n del contrato pÃºblico al contrato de MS-Legajos.
        var msRequest = legajoMapper.toMSRequest(request);

        // La categorÃ­a se envÃ­a como parÃ¡metro de consulta.
        return legajoClient.crear(
                msRequest,
                request.categoriaId(),
                identity);
    }

    // =========================================================
    // RESOLUCIÃ“N DE IDENTIDAD
    // =========================================================

    // =========================================================
    // ACTUALIZACIÃ“N DE LEGAJO
    // =========================================================

    @PutMapping("/{id}")
    public LegajoResponseDTO actualizar(
            @PathVariable UUID id,
            @RequestBody LegajoCreateRequest request,
            HttpServletRequest httpRequest) {

        EasyAuthIdentity identity = obtenerIdentidad(httpRequest);
        var msRequest = legajoMapper.toMSRequest(request);

        return legajoClient.actualizar(
                id,
                msRequest,
                request.categoriaId(),
                identity);
    }
    // =========================================================
    // ARCHIVAR LEGAJO
    // =========================================================

    @PatchMapping("/{id}/archivar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archivar(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {

        EasyAuthIdentity identity = obtenerIdentidad(httpRequest);
        legajoClient.archivar(id, identity);
    }
    // =========================================================
    // REACTIVAR LEGAJO
    // =========================================================

    @PatchMapping("/{id}/reactivar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reactivar(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {

        EasyAuthIdentity identity = obtenerIdentidad(httpRequest);
        legajoClient.reactivar(id, identity);
    }
    private EasyAuthIdentity obtenerIdentidad(HttpServletRequest request) {

        return identityResolver.resolve(request)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "No se pudo obtener la identidad autenticada"));
    }
}
