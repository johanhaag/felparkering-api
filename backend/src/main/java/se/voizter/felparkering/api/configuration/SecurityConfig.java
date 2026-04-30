package se.voizter.felparkering.api.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.http.HttpServletResponse;
import se.voizter.felparkering.api.security.JwtFilter;
import se.voizter.felparkering.api.security.JwtProvider;

/**
 * Konfigurerar inställningar med JWT autentisering.
 * Hanterar vilka endpoints som kräver autentisering och vilka roller som har åtkomst.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtProvider jwtProvider;

    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * Konfigurerar de filter som säkerhetskedjan ska innehålla.
     * - Tillåter alla för inloggning och registrering
     * - Skyddar admin- och rapportendpoints baserat på roll.
     * - Stänger av sessionshantering och använder istället JWT autentisering.
     * 
     * @param http HttpSecurity-instansen som konfigureras.
     * @return Konfigurerad {@link SecurityFilterChain}
     * @throws Exception vid konfigurationsfel
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Inaktiverar CSRF då vi använder JWT.
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth // Definerar behörigheter till olika endpoints
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/health", "/login", "/register").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/attendant/**").hasRole("ATTENDANT")
            .requestMatchers("/home/**").hasRole("CUSTOMER")
            .requestMatchers(HttpMethod.POST, "/addresses/route").hasRole("ATTENDANT")
            .anyRequest().authenticated() // Övriga endpoints kräver autentisering
        )
        .addFilterAfter(new JwtFilter(jwtProvider), SecurityContextHolderFilter.class)
        .exceptionHandling(e -> e
            .authenticationEntryPoint((req, res, ex) -> {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                res.getWriter().write("{\"error\":\"Missing or invalid token\"}");
            })
            .accessDeniedHandler((req, res, ex) -> {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                res.getWriter().write("{\"error\":\"Access Denied\"}");
            })
        );
        return http.build();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(
        @Qualifier("corsConfigurationSource") CorsConfigurationSource source) {
            FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
            bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    @Primary
    CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOriginPatterns(List.of(
                "https://felparkering-api.netlify.app",
                "https://*.netlify.app",
                "http://localhost:*"
            ));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("*"));
            config.setExposedHeaders(List.of("Authorization"));
            config.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", config);
            return source;
    }

}
