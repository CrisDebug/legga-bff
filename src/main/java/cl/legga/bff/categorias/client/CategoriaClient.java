
package cl.legga.bff.categorias.client;

import cl.legga.bff.identidad.model.EasyAuthIdentity;
import cl.legga.bff.categorias.dto.CategoriaCreateRequestDTO;
import cl.legga.bff.categorias.dto.CategoriaPageResponseDTO;
import cl.legga.bff.categorias.dto.CategoriaResponseDTO;
import cl.legga.bff.categorias.dto.CategoriaDocumentoRequeridoCreateRequestDTO;
import cl.legga.bff.categorias.dto.CategoriaDocumentoRequeridoResponseDTO;
import cl.legga.bff.categorias.dto.CategoriaCampoRequestDTO;
import cl.legga.bff.categorias.dto.CategoriaCampoResponseDTO;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.UUID;

@Component
public class CategoriaClient {

        private final RestClient restClient;

        public CategoriaClient(
                        RestClient.Builder builder,
                        @Value("${legga.ms-legajos.base-url}") String baseUrl) {

                this.restClient = builder
                                .baseUrl(baseUrl)
                                .build();
        }

        public CategoriaPageResponseDTO listar(
                        Boolean activa,
                        int page,
                        int size,
                        EasyAuthIdentity identity) {

                SpringPageResponseDTO respuesta = restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/categorias")
                                                .queryParam("activa", activa)
                                                .queryParam("page", page)
                                                .queryParam("size", size)
                                                .build())
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .retrieve()
                                .body(SpringPageResponseDTO.class);

                if (respuesta == null) {
                        return null;
                }

                return new CategoriaPageResponseDTO(
                                respuesta.content(),
                                respuesta.number(),
                                respuesta.size(),
                                respuesta.totalElements(),
                                respuesta.totalPages(),
                                respuesta.first(),
                                respuesta.last());
        }

        public CategoriaResponseDTO crear(
                        CategoriaCreateRequestDTO request,
                        EasyAuthIdentity identity) {

                return restClient.post()
                                .uri("/api/categorias")
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .body(request)
                                .retrieve()
                                .body(CategoriaResponseDTO.class);
        }

        public List<CategoriaDocumentoRequeridoResponseDTO> listarRequisitos(
                        UUID categoriaId,
                        EasyAuthIdentity identity) {

                return restClient.get()
                                .uri("/api/categorias/{categoriaId}/requisitos-documentales",
                                                categoriaId)
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .retrieve()
                                .body(new ParameterizedTypeReference<List<CategoriaDocumentoRequeridoResponseDTO>>() {
                                });
        }

        public CategoriaDocumentoRequeridoResponseDTO crearRequisito(
                        UUID categoriaId,
                        CategoriaDocumentoRequeridoCreateRequestDTO request,
                        EasyAuthIdentity identity) {

                return restClient.post()
                                .uri("/api/categorias/{categoriaId}/requisitos-documentales",
                                                categoriaId)
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .body(request)
                                .retrieve()
                                .body(CategoriaDocumentoRequeridoResponseDTO.class);
        }

        // ==========================================
        // CAMPOS DINÁMICOS
        // ==========================================

        public List<CategoriaCampoResponseDTO> listarCampos(
                        UUID categoriaId,
                        EasyAuthIdentity identity) {

                return restClient.get()
                                .uri("/api/categorias/{categoriaId}/campos", categoriaId)
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .retrieve()
                                .body(new ParameterizedTypeReference<List<CategoriaCampoResponseDTO>>() {
                                });
        }

        public CategoriaCampoResponseDTO crearCampo(
                        UUID categoriaId,
                        CategoriaCampoRequestDTO request,
                        EasyAuthIdentity identity) {

                return restClient.post()
                                .uri("/api/categorias/{categoriaId}/campos", categoriaId)
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .body(request)
                                .retrieve()
                                .body(CategoriaCampoResponseDTO.class);
        }

        public CategoriaCampoResponseDTO actualizarCampo(
                        UUID categoriaId,
                        UUID campoId,
                        CategoriaCampoRequestDTO request,
                        EasyAuthIdentity identity) {

                return restClient.put()
                                .uri("/api/categorias/{categoriaId}/campos/{campoId}",
                                                categoriaId, campoId)
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .body(request)
                                .retrieve()
                                .body(CategoriaCampoResponseDTO.class);
        }

        public void eliminarCampo(
                        UUID categoriaId,
                        UUID campoId,
                        EasyAuthIdentity identity) {

                restClient.delete()
                                .uri("/api/categorias/{categoriaId}/campos/{campoId}",
                                                categoriaId, campoId)
                                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                                .retrieve()
                                .toBodilessEntity();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        private record SpringPageResponseDTO(
                        List<CategoriaResponseDTO> content,
                        int number,
                        int size,
                        long totalElements,
                        int totalPages,
                        boolean first,
                        boolean last) {
        }
}
