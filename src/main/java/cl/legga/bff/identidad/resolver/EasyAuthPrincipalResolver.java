
package cl.legga.bff.identidad.resolver;

import cl.legga.bff.identidad.model.EasyAuthIdentity;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;
import java.util.Set;

@Component
public class EasyAuthPrincipalResolver {

    private final ObjectMapper objectMapper;

    private static final String PRINCIPAL_HEADER = "X-MS-CLIENT-PRINCIPAL";

    private static final Set<String> TENANT_CLAIMS = Set.of(
            "tid",
            "http://schemas.microsoft.com/identity/claims/tenantid");

    private static final Set<String> OBJECT_CLAIMS = Set.of(
            "oid",
            "http://schemas.microsoft.com/identity/claims/objectidentifier");

    private static final Set<String> EMAIL_CLAIMS = Set.of(
            "email",
            "preferred_username",
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress");

    public EasyAuthPrincipalResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<EasyAuthIdentity> resolve(
            HttpServletRequest request) {

        String principal = request.getHeader(PRINCIPAL_HEADER);

        if (principal == null || principal.isBlank()) {
            return Optional.empty();
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(principal);

            JsonNode root = objectMapper.readTree(decoded);
            JsonNode claims = root.path("claims");

            if (!claims.isArray()) {
                return Optional.empty();
            }

            String tenantId = null;
            String objectId = null;
            String email = null;
            String displayName = null;

            for (JsonNode claim : claims) {

                String type = claim.path("typ").asText("");
                String value = claim.path("val").asText("");

                if (value.isBlank()) {
                    continue;
                }

                if (TENANT_CLAIMS.contains(type)) {
                    tenantId = value;
                }

                if (OBJECT_CLAIMS.contains(type)) {
                    objectId = value;
                }

                if (EMAIL_CLAIMS.contains(type) && email == null) {
                    email = value;
                }

                if ("name".equals(type)) {
                    displayName = value;
                }
            }

            if (tenantId == null || objectId == null) {
                return Optional.empty();
            }

            return Optional.of(
                    new EasyAuthIdentity(
                            tenantId,
                            objectId,
                            email,
                            displayName));

        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
