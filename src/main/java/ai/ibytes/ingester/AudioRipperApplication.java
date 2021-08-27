package ai.ibytes.ingester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import ai.ibytes.ingester.config.AppConfig;
import ai.ibytes.ingester.config.StorageConfig;

@SpringBootApplication
@EnableConfigurationProperties({StorageConfig.class, AppConfig.class})
@EnableScheduling
public class AudioRipperApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioRipperApplication.class, args);
	}
}
