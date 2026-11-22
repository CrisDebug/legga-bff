package cl.legga.bff.categorias.client;

import cl.legga.bff.identidad.model.EasyAuthIdentity;
import cl.legga.bff.categorias.dto.CategoriaPageResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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

    /**
     * Consulta categorías filtradas y paginadas en MS-Legajos.
     */
    public CategoriaPageResponseDTO listar(
            Boolean activa,
            int page,
            int size,
            EasyAuthIdentity identity) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/categorias")
                        .queryParam("activa", activa)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                .retrieve()
                .body(CategoriaPageResponseDTO.class);
    }
}
