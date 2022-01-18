package ai.ibytes.ingester.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("auth")
@Data
public class AuthConfig {
    private String username;
    private String password;
}
