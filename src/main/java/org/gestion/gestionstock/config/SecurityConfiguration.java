package org.gestion.gestionstock.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import javax.crypto.spec.SecretKeySpec;

import java.util.HexFormat;
import javax.crypto.SecretKey;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    @Value("${app.jwt.secret-key}")
    private String secret;

    // Rôles (sans le préfixe ROLE_), voir TypeRole
    private static final String ADMIN = "ADMIN_ENTREPRISE";
    private static final String STOCK = "GESTIONNAIRE_STOCK";
    private static final String COMMERCIAL = "COMMERCIAL";
    private static final String CAISSIER = "CAISSIER";
    private static final String CLIENT = "CLIENT";
    private static final String FOURNISSEUR = "FOURNISSEUR";
    /** Personnel de l'entreprise ; UTILISATEUR = accès en lecture seule. */
    private static final String[] EQUIPE = {ADMIN, STOCK, COMMERCIAL, CAISSIER, "UTILISATEUR"};
    private static final String[] EQUIPE_ET_CLIENT = {ADMIN, STOCK, COMMERCIAL, CAISSIER, "UTILISATEUR", CLIENT};
    private static final String[] EQUIPE_ET_FOURNISSEUR = {ADMIN, STOCK, COMMERCIAL, CAISSIER, "UTILISATEUR", FOURNISSEUR};

    /** Lit les rôles dans le claim "roles" du jeton (valeurs déjà préfixées par ROLE_). */
    @Bean
    public org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authorities = new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");
        var converter = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                                // --- Accès public ---
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "/auth/connexion", "/auth/mot-de-passe-oublie",
                                        "/entreprises/create", "/utilisateurs/inscription").permitAll()
                                .requestMatchers(HttpMethod.GET, "/entreprises/public", "/photos/*").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                                // --- Compte de l'utilisateur connecté ---
                                .requestMatchers(HttpMethod.GET, "/utilisateurs/email/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/utilisateurs/update/password").authenticated()
                                .requestMatchers(HttpMethod.POST, "/photos/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/entreprises/*").authenticated()

                                // --- Administration ---
                                .requestMatchers("/utilisateurs/**", "/entreprises/**").hasRole(ADMIN)

                                // --- Catalogue : lecture pour tous, modification par la gestion de stock ---
                                .requestMatchers(HttpMethod.GET, "/articles/historique/**").hasAnyRole(EQUIPE)
                                .requestMatchers(HttpMethod.GET, "/articles/**", "/cathegory/**").authenticated()
                                .requestMatchers("/articles/**", "/cathegory/**").hasAnyRole(ADMIN, STOCK)

                                // --- Stock ---
                                .requestMatchers(HttpMethod.GET, "/mvstk/**").hasAnyRole(EQUIPE)
                                .requestMatchers("/mvstk/**").hasAnyRole(ADMIN, STOCK)

                                // --- Clients et commandes clients ---
                                .requestMatchers(HttpMethod.GET, "/clients/**").hasAnyRole(EQUIPE)
                                .requestMatchers("/clients/**").hasAnyRole(ADMIN, COMMERCIAL)
                                .requestMatchers(HttpMethod.GET, "/commandesclients/**").hasAnyRole(EQUIPE_ET_CLIENT)
                                .requestMatchers(HttpMethod.POST, "/commandesclients/create").hasAnyRole(ADMIN, COMMERCIAL, CLIENT)
                                .requestMatchers("/commandesclients/**").hasAnyRole(ADMIN, COMMERCIAL)

                                // --- Fournisseurs et commandes fournisseurs ---
                                .requestMatchers(HttpMethod.GET, "/fournisseurs/**").hasAnyRole(EQUIPE)
                                .requestMatchers("/fournisseurs/**").hasAnyRole(ADMIN, STOCK)
                                .requestMatchers(HttpMethod.GET, "/commandesfournisseurs/**").hasAnyRole(EQUIPE_ET_FOURNISSEUR)
                                .requestMatchers("/commandesfournisseurs/**").hasAnyRole(ADMIN, STOCK)

                                // --- Ventes ---
                                .requestMatchers(HttpMethod.GET, "/ventes/**").hasAnyRole(EQUIPE)
                                .requestMatchers("/ventes/**").hasAnyRole(ADMIN, COMMERCIAL, CAISSIER)

                                .anyRequest().authenticated()
                        ).oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider( UserDetailsService userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
}

    @Bean
    JwtEncoder jwtEncoder() {

        byte[] keyBytes = HexFormat.of().parseHex(secret);

        SecretKey key = new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );

        return new NimbusJwtEncoder(
                new ImmutableSecret<>(key)
        );
    }

    @Bean
    JwtDecoder jwtDecoder() {

        byte[] keyBytes = HexFormat.of().parseHex(secret);

        SecretKey key = new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );

        return NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}