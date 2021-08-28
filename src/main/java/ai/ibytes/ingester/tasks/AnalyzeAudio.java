package ai.ibytes.ingester.tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.util.ExternalProcess;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled tasks to generate a waveform and stats
 * @author kinderk
 */
@Component
@Slf4j
public class AnalyzeAudio {

    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private ExternalProcess externalProcess;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(initialDelay = 3000, fixedRate = 15000)
    public void generateWaveform()  {
        log.info("Generating waveforms");

        storageService.loadAll().forEach(path -> {
            try {
                FileUpload f = (FileUpload)objectMapper.readValue(path.toAbsolutePath().toFile(), FileUpload.class);

                // Does the file have a {filename}.jpg
                if(!Files.exists(Path.of(storageService.getRootLocation().toString(), f.getFilename() + ".png")))   {
                    log.info( "{} needs a waveform, generating", f.getFilename());
                    externalProcess.generateWaveform(f.getFilename());
                    f.setWaveform(true);
                    storageService.save(f);
                    log.info( "Finished generating waveform for {}", f.getFilename());
                }

            } catch (IOException | InterruptedException e) {
                log.error("Unable to generate waveform for {}", path);
            }
        });

        log.info("Finished generating waveforms");
    }
}
