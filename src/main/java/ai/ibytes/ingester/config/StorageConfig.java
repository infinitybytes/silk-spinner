package ai.ibytes.ingester.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("storage")
@Data
public class StorageConfig {
    private String ftpHost;
    private String dataFiles;
    private String analysisStore;
}
