
package cl.legga.bff.auth.controller;

import cl.legga.bff.identidad.resolver.EasyAuthPrincipalResolver;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthDiagnosticController {

    private final ObjectMapper objectMapper;
    private final EasyAuthPrincipalResolver identityResolver;

    private static final Set<String> TENANT_CLAIMS = Set.of(
            "tid",
            "http://schemas.microsoft.com/identity/claims/tenantid");

    private static final Set<String> OBJECT_CLAIMS = Set.of(
            "oid",
            "http://schemas.microsoft.com/identity/claims/objectidentifier");

    public AuthDiagnosticController(
            ObjectMapper objectMapper,
            EasyAuthPrincipalResolver identityResolver) {
        this.objectMapper = objectMapper;
        this.identityResolver = identityResolver;
    }

    @GetMapping("/diagnostic")
    public Map<String, Object> diagnostic(HttpServletRequest request) {

        String principal = request.getHeader("X-MS-CLIENT-PRINCIPAL");

        List<String> claimTypes = new ArrayList<>();
        boolean decoded = false;
        boolean hasTenantId = false;
        boolean hasObjectId = false;

        if (principal != null && !principal.isBlank()) {
            try {
                byte[] jsonBytes = Base64.getDecoder().decode(principal);
                JsonNode root = objectMapper.readTree(jsonBytes);
                JsonNode claims = root.path("claims");

                if (claims.isArray()) {
                    decoded = true;

                    for (JsonNode claim : claims) {
                        String type = claim.path("typ").asText("");

                        if (!type.isBlank()) {
                            claimTypes.add(type);
                            hasTenantId |= TENANT_CLAIMS.contains(type);
                            hasObjectId |= OBJECT_CLAIMS.contains(type);
                        }
                    }
                }
            } catch (Exception ignored) {
                decoded = false;
                claimTypes.clear();
                hasTenantId = false;
                hasObjectId = false;
            }
        }

        var identity = identityResolver.resolve(request);

        return Map.of(
                "service", "bff-legga",
                "endpoint", "auth-diagnostic",
                "principalDecoded", decoded,
                "claimTypes", claimTypes,
                "tenantIdClaimFound", hasTenantId,
                "objectIdClaimFound", hasObjectId,
                "tenantId", identity.map(i -> i.tenantId()).orElse("NOT_FOUND"),
                "objectId", identity.map(i -> i.objectId()).orElse("NOT_FOUND"));
    }
}
