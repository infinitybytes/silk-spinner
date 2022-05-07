package ai.ibytes.ingester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import ai.byts.giga.App;
import ai.ibytes.ingester.config.AppConfig;
import ai.ibytes.ingester.config.StorageConfig;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableConfigurationProperties({StorageConfig.class, AppConfig.class})
@EnableScheduling
@Slf4j
public class AudioRipperApplication {

	public static void main(String[] args) {
		Runnable platform = new Runnable() {

			@Override
			public void run() {
				// Start up gbyts
				App gigaByts = new App();
				try {
					gigaByts.startPlatform(args);
				}
				catch( Exception e )	{
					log.error("Error starting GIGABYTS",e);
				}
			}
		};

		Thread platformThread = new Thread(platform);
		platformThread.start();
		
		SpringApplication.run(AudioRipperApplication.class, args);
	}
}
