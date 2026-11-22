package cl.legga.bff.documentos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DocumentoClient {

    private final RestClient restClient;

    public DocumentoClient(
            RestClient.Builder builder,
            @Value("${legga.ms-documentos.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public List<Map<String, Object>> buscarPorLegajo(UUID legajoId) {

        return restClient.get()
                .uri("/api/documentos/legajo/{legajoId}", legajoId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });
    }

    public Map<String, Object> buscarPorId(UUID id) {

        return restClient.get()
                .uri("/api/documentos/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {
                });
    }

    public Map<String, Object> generarEnlaceDescarga(UUID id) {
        return restClient.get()
                .uri("/api/documentos/{id}/descarga", id)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {
                });
    }
}