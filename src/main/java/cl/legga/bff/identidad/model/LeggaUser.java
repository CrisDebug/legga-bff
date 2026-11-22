package cl.legga.bff.identidad.model;

import java.util.Set;
import java.util.UUID;

public record LeggaUser(
        UUID userId,
        UUID institutionId,
        UUID profileId,
        String profileCode,
        Set<String> permissions,
        boolean active) {
    public LeggaUser {
        permissions = permissions == null
                ? Set.of()
                : Set.copyOf(permissions);
    }
}