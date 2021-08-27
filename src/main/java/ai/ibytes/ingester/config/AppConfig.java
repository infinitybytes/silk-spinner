package ai.ibytes.ingester.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("app")
@Data
public class AppConfig {
    private String rootDir;
}
