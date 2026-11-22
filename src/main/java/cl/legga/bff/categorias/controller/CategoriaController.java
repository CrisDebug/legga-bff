package cl.legga.bff.categorias.controller;

import cl.legga.bff.categorias.client.CategoriaClient;
import cl.legga.bff.categorias.dto.CategoriaPageResponseDTO;
import cl.legga.bff.identidad.model.EasyAuthIdentity;
import cl.legga.bff.identidad.resolver.EasyAuthPrincipalResolver;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaClient categoriaClient;
    private final EasyAuthPrincipalResolver identityResolver;

    public CategoriaController(
            CategoriaClient categoriaClient,
            EasyAuthPrincipalResolver identityResolver) {
        this.categoriaClient = categoriaClient;
        this.identityResolver = identityResolver;
    }

    @GetMapping
    public CategoriaPageResponseDTO listar(
            @RequestParam(required = false) Boolean activa,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            HttpServletRequest request) {

        EasyAuthIdentity identity = identityResolver.resolve(request)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "No se pudo obtener la identidad autenticada"));

        return categoriaClient.listar(activa, page, size, identity);
    }
}
