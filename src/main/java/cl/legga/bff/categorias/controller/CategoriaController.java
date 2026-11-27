
package cl.legga.bff.categorias.controller;

import cl.legga.bff.categorias.client.CategoriaClient;
import cl.legga.bff.categorias.dto.CategoriaPageResponseDTO;
import cl.legga.bff.categorias.dto.CategoriaCreateRequestDTO;
import cl.legga.bff.categorias.dto.CategoriaResponseDTO;
import cl.legga.bff.categorias.dto.CategoriaDocumentoRequeridoCreateRequestDTO;
import cl.legga.bff.categorias.dto.CategoriaDocumentoRequeridoResponseDTO;
import cl.legga.bff.categorias.dto.CategoriaCampoRequestDTO;
import cl.legga.bff.categorias.dto.CategoriaCampoResponseDTO;
import cl.legga.bff.identidad.model.EasyAuthIdentity;
import cl.legga.bff.identidad.resolver.EasyAuthPrincipalResolver;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public CategoriaResponseDTO crear(
                        @RequestBody CategoriaCreateRequestDTO requestDTO,
                        HttpServletRequest request) {

                EasyAuthIdentity identity = identityResolver.resolve(request)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "No se pudo obtener la identidad autenticada"));

                return categoriaClient.crear(requestDTO, identity);
        }

        @GetMapping("/{categoriaId}/requisitos-documentales")
        public List<CategoriaDocumentoRequeridoResponseDTO> listarRequisitos(
                        @PathVariable UUID categoriaId,
                        HttpServletRequest request) {

                EasyAuthIdentity identity = identityResolver.resolve(request)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "No se pudo obtener la identidad autenticada"));

                return categoriaClient.listarRequisitos(categoriaId, identity);
        }

        @PostMapping("/{categoriaId}/requisitos-documentales")
        @ResponseStatus(HttpStatus.CREATED)
        public CategoriaDocumentoRequeridoResponseDTO crearRequisito(
                        @PathVariable UUID categoriaId,
                        @RequestBody CategoriaDocumentoRequeridoCreateRequestDTO requestDTO,
                        HttpServletRequest request) {

                EasyAuthIdentity identity = identityResolver.resolve(request)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "No se pudo obtener la identidad autenticada"));

                return categoriaClient.crearRequisito(categoriaId, requestDTO, identity);
        }

        // ==========================================
        // CAMPOS DINÁMICOS
        // ==========================================

        @GetMapping("/{categoriaId}/campos")
        public List<CategoriaCampoResponseDTO> listarCampos(
                        @PathVariable UUID categoriaId,
                        HttpServletRequest request) {

                EasyAuthIdentity identity = identityResolver.resolve(request)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "No se pudo obtener la identidad autenticada"));

                return categoriaClient.listarCampos(categoriaId, identity);
        }

        @PostMapping("/{categoriaId}/campos")
        @ResponseStatus(HttpStatus.CREATED)
        public CategoriaCampoResponseDTO crearCampo(
                        @PathVariable UUID categoriaId,
                        @RequestBody CategoriaCampoRequestDTO requestDTO,
                        HttpServletRequest request) {

                EasyAuthIdentity identity = identityResolver.resolve(request)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "No se pudo obtener la identidad autenticada"));

                return categoriaClient.crearCampo(categoriaId, requestDTO, identity);
        }

        @PutMapping("/{categoriaId}/campos/{campoId}")
        public CategoriaCampoResponseDTO actualizarCampo(
                        @PathVariable UUID categoriaId,
                        @PathVariable UUID campoId,
                        @RequestBody CategoriaCampoRequestDTO requestDTO,
                        HttpServletRequest request) {

                EasyAuthIdentity identity = identityResolver.resolve(request)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "No se pudo obtener la identidad autenticada"));

                return categoriaClient.actualizarCampo(
                                categoriaId, campoId, requestDTO, identity);
        }

        @DeleteMapping("/{categoriaId}/campos/{campoId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void eliminarCampo(
                        @PathVariable UUID categoriaId,
                        @PathVariable UUID campoId,
                        HttpServletRequest request) {

                EasyAuthIdentity identity = identityResolver.resolve(request)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "No se pudo obtener la identidad autenticada"));

                categoriaClient.eliminarCampo(categoriaId, campoId, identity);
        }
}
