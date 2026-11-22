
package cl.legga.bff.legajos.client;

// Identidad validada previamente por Easy Auth.
import cl.legga.bff.identidad.model.EasyAuthIdentity;

// DTOs utilizados para las solicitudes y respuestas de legajos.
import cl.legga.bff.legajos.dto.LegajoCreateMSRequest;
import cl.legga.bff.legajos.dto.LegajoPageResponseDTO;
import cl.legga.bff.legajos.dto.LegajoResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

/**
 * Cliente HTTP encargado de centralizar la comunicación
 * entre el BFF de LEGGA y el microservicio MS-Legajos.
 */
@Component
public class LegajoClient {

        private final RestClient restClient;

        public LegajoClient(
                        RestClient.Builder builder,
                        @Value("${legga.ms-legajos.base-url}") String baseUrl) {

                this.restClient = builder
                                .baseUrl(baseUrl)
                                .build();
        }

        // =========================================================
        // CONSULTA PAGINADA DE LEGAJOS
        // =========================================================

        public LegajoPageResponseDTO listar(EasyAuthIdentity identity) {

                return restClient.get()
                                .uri("/api/legajos")
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .retrieve()
                                .body(LegajoPageResponseDTO.class);
        }

        // =========================================================
        // CONSULTA DE DETALLE DE LEGAJO
        // =========================================================

        public LegajoResponseDTO buscarPorId(
                        UUID id,
                        EasyAuthIdentity identity) {

                return restClient.get()
                                .uri("/api/legajos/{id}", id)
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .retrieve()
                                .body(LegajoResponseDTO.class);
        }

        // =========================================================
        // CREACIÓN DE LEGAJO
        // =========================================================

        public LegajoResponseDTO crear(
                        LegajoCreateMSRequest legajo,
                        UUID categoriaId,
                        EasyAuthIdentity identity) {

                return restClient.post()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/legajos")
                                                .queryParam("categoriaId", categoriaId)
                                                .build())
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .body(legajo)
                                .retrieve()
                                .body(LegajoResponseDTO.class);
        }

        // =========================================================
        // CONSULTA DE CONTEXTO DE IDENTIDAD
        // =========================================================

        public ContextoIdentidadResponse consultarContexto(
                        EasyAuthIdentity identity) {

                return restClient.post()
                                .uri("/api/identidad/contexto")
                                .body(new ContextoIdentidadRequest(
                                                identity.tenantId(),
                                                identity.objectId()))
                                .retrieve()
                                .body(ContextoIdentidadResponse.class);
        }

        // =========================================================
        // DTO DE SOLICITUD DE IDENTIDAD
        // =========================================================

        public record ContextoIdentidadRequest(
                        String tenantId,
                        String objectId) {
        }

        // =========================================================
        // DTO DE RESPUESTA DE IDENTIDAD
        // =========================================================

        public record ContextoIdentidadResponse(
                        UUID usuarioId,
                        String correo,
                        String nombre,
                        Boolean activo,
                        UUID institucionId,
                        String institucionCodigo,
                        String institucionNombre,
                        UUID perfilId,
                        String perfilCodigo,
                        String perfilNombre,
                        List<PermisoDTO> permisos) {
        }

        // =========================================================
        // DTO DE PERMISOS
        // =========================================================

        public record PermisoDTO(
                        String codigo,
                        Boolean permitido) {
        }
}
