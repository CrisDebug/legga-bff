
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
 * Cliente HTTP encargado de centralizar la comunicaciÃ³n
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

        public LegajoPageResponseDTO listar(
                EasyAuthIdentity identity,
                String q,
                UUID categoriaId,
                int page,
                int size) {

                return restClient.get()
                                .uri(uriBuilder -> {
                                        var builder = uriBuilder.path("/api/legajos");

                                        if (q != null && !q.isBlank()) {
                                                builder.queryParam("q", q);
                                        }

                                        if (categoriaId != null) {
                                                builder.queryParam("categoriaId", categoriaId);
                                        }

                                        return builder
                                                        .queryParam("page", page)
                                                        .queryParam("size", size)
                                                        .build();
                                })
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
        // CREACIÃ“N DE LEGAJO
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

        // =========================================================
        // ACTUALIZACIÃ“N DE LEGAJO
        // =========================================================

        public LegajoResponseDTO actualizar(
                        UUID id,
                        LegajoCreateMSRequest legajo,
                        UUID categoriaId,
                        EasyAuthIdentity identity) {

                return restClient.put()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/legajos/{id}")
                                                .queryParam("categoriaId", categoriaId)
                                                .build(id))
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .body(legajo)
                                .retrieve()
                                .body(LegajoResponseDTO.class);
        }
        // =========================================================
        // ARCHIVAR LEGAJO
        // =========================================================

        public void archivar(
                        UUID id,
                        EasyAuthIdentity identity) {

                restClient.patch()
                                .uri("/api/legajos/{id}/archivar", id)
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .retrieve()
                                .toBodilessEntity();
        }
        // =========================================================
        // REACTIVAR LEGAJO
        // =========================================================

        public void reactivar(
                        UUID id,
                        EasyAuthIdentity identity) {

                restClient.patch()
                                .uri("/api/legajos/{id}/reactivar", id)
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .retrieve()
                                .toBodilessEntity();
        }
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
