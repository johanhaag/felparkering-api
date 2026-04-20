package se.voizter.felparkering.api.security;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT-filter som körs en gång per inkommande request för att kontrollera JWT-token.
 */
public class JwtFilter extends OncePerRequestFilter{
    private final JwtProvider jwtProvider;

    /**
     * Skapar en ny instans av {@code JwtFilter}.
     * 
     * @param jwtProvider komponent för att validera och tolka JWT-token.
     * @param userRepository repository för att slå upp användare via id.
     */
    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }
    
    /**
     * Filtrerar varje inkommande HTTP-request för att kontrollera JWT-token.
     * 
     * Om Authorization-headern innehåller en giltig token, valideras den och autentiserar användaren. 
     * En {@link UsernamePasswordAuthenticationToken} lagras i {@link SecurityContextHolder} för att ge tillgång till skyddade uppgifter.
     * 
     * @param request inkommande HTTP-request.
     * @param response HTTP-responsen.
     * @param chain filterkedjan som fortsätter request-processen.
     * @throws ServletException om något servletrelaterat fel uppstår.
     * @throws IOException om något IO-fel uppstår.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws ServletException, IOException {

        String bearer = req.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7);

            if (jwtProvider.validateToken(token)) {
                Long id = jwtProvider.getId(token);
                String role  = Optional.ofNullable(jwtProvider.getRole(token))
                    .orElse("")
                    .trim()
                    .toUpperCase(Locale.ROOT);
                String granted = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                var auth = new UsernamePasswordAuthenticationToken(
                    id, null, List.of(new SimpleGrantedAuthority(granted)));

                SecurityContextHolderStrategy strategy = SecurityContextHolder.getContextHolderStrategy();
                SecurityContext context = strategy.createEmptyContext();
                context.setAuthentication(auth);
                strategy.setContext(context);
            }
        }

        chain.doFilter(req, res);
    }
}
