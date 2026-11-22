
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
 */
@Configuration
public class SecurityConfig {

        @Value("${legga.cors.allowed-origins}")
        private String allowedOrigins;

        @Value("${legga.security.dev-mode:false}")
        private boolean devMode;

        /**
         * Configuración CORS para el frontend Angular.
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                // Orígenes autorizados según el perfil activo.
                configuration.setAllowedOrigins(
                                Arrays.stream(allowedOrigins.split(","))
                                                .map(String::trim)
                                                .filter(origin -> !origin.isEmpty())
                                                .toList());

                // Métodos HTTP permitidos.
                configuration.setAllowedMethods(List.of(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "OPTIONS"));

                // Headers permitidos, incluidos los de contexto LEGGA.
                configuration.setAllowedHeaders(List.of(
                                "Content-Type",
                                "Authorization",
                                "X-XSRF-TOKEN",
                                "X-Requested-With",
                                "Accept",
                                "X-LEGGA-TENANT-ID",
                                "X-LEGGA-OBJECT-ID"));

                // Permite el envío de cookies.
                configuration.setAllowCredentials(true);

                // Tiempo de caché de la configuración CORS.
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        /**
         * Reglas de seguridad del BFF según el modo de ejecución.
         */

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)
                        throws Exception {

                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // CSRF continúa habilitado.
                                // Solo se excluye el POST de creación en desarrollo.
                                .csrf(csrf -> {
                                        csrf.ignoringRequestMatchers("/actuator/**");

                                        if (devMode) {
                                                csrf.ignoringRequestMatchers(
                                                                request -> "POST".equals(request.getMethod())
                                                                                && "/api/v1/legajos".equals(request
                                                                                                .getServletPath()));
                                        }
                                })

                                .authorizeHttpRequests(auth -> {

                                        auth.requestMatchers(
                                                        "/actuator/health",
                                                        "/actuator/health/**",
                                                        "/actuator/info",
                                                        "/error",
                                                        "/api/v1/auth/diagnostic")
                                                        .permitAll();

                                        if (devMode) {

                                                auth.requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/v1/identidad/contexto",
                                                                "/api/v1/legajos",
                                                                "/api/v1/legajos/**",
                                                                "/api/v1/categorias",
                                                                "/api/v1/categorias/**")
                                                                .permitAll();

                                                // Creación habilitada en desarrollo.
                                                auth.requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/v1/legajos")
                                                                .permitAll();

                                        } else {

                                                auth.requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/v1/identidad/contexto",
                                                                "/api/v1/legajos",
                                                                "/api/v1/legajos/**",
                                                                "/api/v1/categorias",
                                                                "/api/v1/categorias/**")
                                                                .authenticated();
                                        }

                                        auth.anyRequest().authenticated();
                                });

                return http.build();
        }

}
