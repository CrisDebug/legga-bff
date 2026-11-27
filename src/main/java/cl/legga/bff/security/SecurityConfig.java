
package cl.legga.bff.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración de seguridad y CORS del BFF LEGGA.
 *
 * Define:
 * - Orígenes y métodos permitidos para Angular.
 * - Protección CSRF.
 * - Reglas de autorización según el modo de ejecución.
 */
@Configuration
public class SecurityConfig {

        // ==================================================
        // CONFIGURACIÓN GENERAL
        // ==================================================

        @Value("${legga.cors.allowed-origins}")
        private String allowedOrigins;

        @Value("${legga.security.dev-mode:false}")
        private boolean devMode;

        // ==================================================
        // CONFIGURACIÓN CORS
        // ==================================================

        /**
         * Configuración CORS para permitir la comunicación
         * entre el frontend Angular y el backend BFF.
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                Arrays.stream(allowedOrigins.split(","))
                                                .map(String::trim)
                                                .filter(origin -> !origin.isEmpty())
                                                .toList());

                configuration.setAllowedMethods(List.of(
                                "GET",
                                "POST",
                                "PUT",
                                "PATCH",
                                "DELETE",
                                "OPTIONS"));

                configuration.setAllowedHeaders(List.of(
                                "Content-Type",
                                "Authorization",
                                "X-XSRF-TOKEN",
                                "X-Requested-With",
                                "Accept",
                                "X-LEGGA-TENANT-ID",
                                "X-LEGGA-OBJECT-ID"));

                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        // ==================================================
        // CONFIGURACIÓN DE SEGURIDAD
        // ==================================================

        /**
         * Configura la cadena de filtros de seguridad del BFF.
         *
         * En desarrollo se permiten determinadas operaciones
         * para facilitar las pruebas desde Angular.
         *
         * En producción se mantienen las rutas protegidas
         * mediante autenticación.
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)
                        throws Exception {

                http
                                // ----------------------------------
                                // CORS
                                // ----------------------------------
                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                // ----------------------------------
                                // PROTECCIÓN CSRF
                                // ----------------------------------
                                // CSRF permanece habilitado globalmente.
                                // Solo se excluyen endpoints específicos.
                                .csrf(csrf -> {

                                        // Exclusión para endpoints de monitoreo.
                                        csrf.ignoringRequestMatchers("/actuator/**");

                                        if (devMode) {

                                                // Exclusiones CSRF para operaciones
                                                // de desarrollo sobre legajos.
                                                csrf.ignoringRequestMatchers(

                                                                // Crear legajo.
                                                                request -> "POST".equals(request.getMethod())
                                                                                && "/api/v1/legajos".equals(
                                                                                                request.getServletPath()),

                                                                // Actualizar legajo.
                                                                request -> "PUT".equals(request.getMethod())
                                                                                && request.getServletPath().startsWith(
                                                                                                "/api/v1/legajos/"),

                                                                // Actualización parcial de legajo.
                                                                request -> "PATCH".equals(request.getMethod())
                                                                                && request.getServletPath().startsWith(
                                                                                                "/api/v1/legajos/"),

                                                                // Crear categoría.
                                                                request -> "POST".equals(request.getMethod())
                                                                                && "/api/v1/categorias".equals(
                                                                                                request.getServletPath()),

                                                                // Crear requisito documental.
                                                                request -> "POST".equals(request.getMethod())
                                                                                && request.getServletPath().matches(
                                                                                                "/api/v1/categorias/[^/]+/requisitos-documentales"),

                                                                // Crear campo dinámico.
                                                                request -> "POST".equals(request.getMethod())
                                                                                && request.getServletPath().matches(
                                                                                                "/api/v1/categorias/[^/]+/campos"),

                                                                // Actualizar campo dinámico.
                                                                request -> "PUT".equals(request.getMethod())
                                                                                && request.getServletPath().matches(
                                                                                                "/api/v1/categorias/[^/]+/campos/[^/]+"),

                                                                // Eliminar campo dinámico.
                                                                request -> "DELETE".equals(request.getMethod())
                                                                                && request.getServletPath().matches(
                                                                                                "/api/v1/categorias/[^/]+/campos/[^/]+"),

                                                                // Cargar documento.
                                                                request -> "POST".equals(request.getMethod())
                                                                                && "/api/v1/documentos".equals(
                                                                                                request.getServletPath()));
                                        }
                                })

                                // ----------------------------------
                                // AUTORIZACIÓN
                                // ----------------------------------
                                .authorizeHttpRequests(auth -> {

                                        // Endpoints públicos generales.
                                        auth.requestMatchers(
                                                        "/actuator/health",
                                                        "/actuator/health/**",
                                                        "/actuator/info",
                                                        "/error",
                                                        "/api/v1/auth/diagnostic")
                                                        .permitAll();

                                        if (devMode) {

                                                // Consultas GET permitidas en desarrollo.
                                                auth.requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/v1/identidad/contexto",
                                                                "/api/v1/legajos",
                                                                "/api/v1/legajos/**",
                                                                "/api/v1/categorias",
                                                                "/api/v1/categorias/**",
                                                                "/api/v1/documentos",
                                                                "/api/v1/documentos/**")
                                                                .permitAll();

                                                // Creación de legajos y categorías.
                                                auth.requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/v1/legajos",
                                                                "/api/v1/categorias")
                                                                .permitAll();

                                                // Creación de requisitos documentales.
                                                auth.requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/v1/categorias/*/requisitos-documentales")
                                                                .permitAll();

                                                // Administración de campos dinámicos.
                                                auth.requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/v1/categorias/*/campos")
                                                                .permitAll();

                                                auth.requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/v1/categorias/*/campos/*")
                                                                .permitAll();

                                                auth.requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/v1/categorias/*/campos/*")
                                                                .permitAll();

                                                // Actualización de legajos.
                                                auth.requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/v1/legajos/**")
                                                                .permitAll();

                                                // Actualización parcial de legajos.
                                                auth.requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/api/v1/legajos/**")
                                                                .permitAll();

                                                // Carga de documentos permitida en desarrollo.
                                                auth.requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/v1/documentos")
                                                                .permitAll();

                                        } else {

                                                // En producción las consultas
                                                // requieren autenticación.
                                                auth.requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/v1/identidad/contexto",
                                                                "/api/v1/legajos",
                                                                "/api/v1/legajos/**",
                                                                "/api/v1/categorias",
                                                                "/api/v1/categorias/**")
                                                                .authenticated();
                                        }

                                        // Cualquier otra solicitud requiere autenticación.
                                        auth.anyRequest().authenticated();
                                });

                return http.build();
        }
}