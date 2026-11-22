package cl.legga.bff.identidad.controller;

// Cliente HTTP que consulta el contexto en MS-Legajos.
import cl.legga.bff.legajos.client.LegajoClient;

// Resolver que obtiene la identidad validada por Easy Auth.
import cl.legga.bff.identidad.resolver.EasyAuthPrincipalResolver;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador encargado de entregar el contexto de identidad
 * del usuario autenticado en LEGGA.
 *
 * Obtiene la identidad desde Easy Auth y consulta a MS-Legajos
 * para recuperar los datos del usuario, institución, perfil
 * y permisos funcionales.
 */
@RestController
@RequestMapping("/api/v1/identidad")
public class IdentidadController {

    // Obtiene los identificadores del usuario desde Easy Auth.
    private final EasyAuthPrincipalResolver identityResolver;

    // Consulta el contexto de identidad en MS-Legajos.
    private final LegajoClient legajoClient;

    /**
     * Constructor con inyección de dependencias.
     */
    public IdentidadController(
            EasyAuthPrincipalResolver identityResolver,
            LegajoClient legajoClient) {

        this.identityResolver = identityResolver;
        this.legajoClient = legajoClient;
    }

    /**
     * Consulta el contexto del usuario autenticado.
     *
     * Flujo:
     * 1. Recibe la solicitud del frontend.
     * 2. Obtiene la identidad mediante Easy Auth.
     * 3. Consulta MS-Legajos.
     * 4. Devuelve los datos y permisos del usuario.
     *
     * No recibe identificadores enviados desde Angular.
     */
    @GetMapping("/contexto")
    public ResponseEntity<?> consultarContexto(
            HttpServletRequest request) {

        // Resolver la identidad autenticada.
        var identity = identityResolver.resolve(request);

        // Rechazar la solicitud si no existe identidad válida.
        if (identity.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("No se pudo obtener la identidad autenticada.");
        }

        // Consultar el contexto de identidad en MS-Legajos.
        var contexto = legajoClient.consultarContexto(identity.get());

        // Entregar el contexto al frontend.
        return ResponseEntity.ok(contexto);
    }
}
