package ai.ibytes.ingester.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("storage")
@Data
public class StorageConfig {
    private String diskLocation;
    private String zipLocation;
    private String tempLocation;
}
