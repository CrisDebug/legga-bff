
package cl.legga.bff.documentos.client;

import cl.legga.bff.identidad.model.EasyAuthIdentity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    /**
     * Carga un documento asociado a un requisito documental.
     */
    public Map<String, Object> cargarDocumento(
            MultipartFile archivo,
            UUID legajoId,
            UUID requisitoId,
            String uploadedBy,
            EasyAuthIdentity identity) throws IOException {

        if (identity == null
                || identity.tenantId() == null
                || identity.objectId() == null) {

            throw new IllegalArgumentException(
                    "La identidad autenticada es obligatoria.");
        }

        if (legajoId == null || requisitoId == null) {
            throw new IllegalArgumentException(
                    "El legajo y el requisito son obligatorios.");
        }

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        ByteArrayResource archivoResource = new ByteArrayResource(archivo.getBytes()) {

            @Override
            public String getFilename() {
                String nombre = archivo.getOriginalFilename();
                return nombre != null ? nombre : "archivo";
            }
        };

        MediaType tipoContenido = MediaType.APPLICATION_OCTET_STREAM;

        if (archivo.getContentType() != null
                && !archivo.getContentType().isBlank()) {

            tipoContenido = MediaType.parseMediaType(
                    archivo.getContentType());
        }

        bodyBuilder.part("archivo", archivoResource)
                .filename(archivoResource.getFilename())
                .contentType(tipoContenido);

        bodyBuilder.part("legajoId", legajoId.toString());
        bodyBuilder.part("requisitoId", requisitoId.toString());
        bodyBuilder.part("uploadedBy", uploadedBy);

        return restClient.post()
                .uri("/api/documentos")
                .header("X-LEGGA-TENANT-ID", identity.tenantId())
                .header("X-LEGGA-OBJECT-ID", identity.objectId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(bodyBuilder.build())
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {
                });
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

    public List<Map<String, Object>> buscarPorRequisito(UUID requisitoId) {

        return restClient.get()
                .uri("/api/documentos/requisito/{requisitoId}", requisitoId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });
    }
}
