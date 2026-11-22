
package cl.legga.bff.identidad.model;

public record EasyAuthIdentity(
        String tenantId,
        String objectId,
        String email,
        String displayName) {
}
