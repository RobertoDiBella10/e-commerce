package progetto.ecommerce.configurations;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8182")
                .realm("ecommerce")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("spring-client")
                .username("admin")
                .password("admin")
                .scope("openid")
                .build();
    }

}
