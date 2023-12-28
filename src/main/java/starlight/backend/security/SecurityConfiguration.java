package starlight.backend.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import starlight.backend.user.repository.UserRepository;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@AllArgsConstructor
class SecurityConfiguration {
    private MapperSecurity mapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(c -> c
                /////////////////////////Tests/////////////////////////////////////////////////////
                .requestMatchers("/test").permitAll()
                .requestMatchers(antMatcher("/h2/**")).permitAll()
                /////////////////////////OpenApi///////////////////////////////////////////////////
                .requestMatchers(antMatcher("/api-docs/**")).permitAll()
                .requestMatchers(antMatcher("/swagger-resources/**")).permitAll()
                .requestMatchers(antMatcher("/configuration/**")).permitAll()
                .requestMatchers(antMatcher("/swagger*/**")).permitAll()
                .requestMatchers(antMatcher("/webjars/**")).permitAll()
                /////////////////////////DevOps////////////////////////////////////////////////////
                .requestMatchers("/error").permitAll()
                /////////////////////////Email/////////////////////////////////////////////////////
                .requestMatchers("/api/v1/sponsors/forgot-password").permitAll()
                .requestMatchers("/api/v1/sponsors/recovery-password").permitAll()
                /////////////////////////Actuator//////////////////////////////////////////////////
                .requestMatchers(antMatcher("/actuator/**")).permitAll()
                /////////////////////////Production////////////////////////////////////////////////
                .requestMatchers("/api/v1/skills").permitAll()
                .requestMatchers("/api/v1/talents").permitAll()
                .requestMatchers("/api/v1/sponsors").permitAll()
                .requestMatchers("/api/v1/admin").permitAll()
                .requestMatchers("/api/v2/talents").permitAll()
                .requestMatchers("/api/v1/proofs").permitAll()
                .requestMatchers("/api/v2/proofs").permitAll()
                .requestMatchers(POST, "/api/v1/talents/login").permitAll()
                .requestMatchers(POST, "/api/v1/sponsors/login").permitAll()
                .requestMatchers(POST, "/api/v1/admin/login").permitAll()
                .requestMatchers(antMatcher("/api/v1/proofs/**")).permitAll()
                .requestMatchers("/api/v1/sponsors/recovery-account").permitAll()
                /////////////////////////Another///////////////////////////////////////////////////
                .requestMatchers(antMatcher("/**")).hasAuthority("ROLE_ADMIN")
                /////////////////////////Another///////////////////////////////////////////////////
                .anyRequest().authenticated()
        );
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.httpBasic();
        http.csrf().disable();
        http.cors();
        http.headers().frameOptions().disable();
        http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .exceptionHandling(c -> c
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public KeyPair keyPair() throws NoSuchAlgorithmException {
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @Bean
    JwtDecoder jwtDecoder(KeyPair keyPair) {
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
    }

    @Bean
    JwtEncoder jwtEncoder(KeyPair keyPair) {
        var jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).privateKey(keyPair.getPrivate()).build();
        var jwkSet = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSet);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> {
            if (userRepository.existsByAdmin_Email(email)) {
                return mapper.toUserDetailsImplAdmin(userRepository.findByAdmin_Email(email));
            }else if(userRepository.existsBySponsor_Email(email)){
                return mapper.toUserDetailsImplSponsor(userRepository.findBySponsor_Email(email));
            }else {
                return mapper.toUserDetailsImplTalent(userRepository.findByTalent_Email(email));
            }
        };
    }
}