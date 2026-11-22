package cl.legga.bff.identidad.service;

import cl.legga.bff.identidad.model.EasyAuthIdentity;
import cl.legga.bff.identidad.model.LeggaUser;

import java.util.Optional;

public interface LeggaIdentityService {

    Optional<LeggaUser> findByExternalIdentity(
            EasyAuthIdentity identity);
}