package ai.ibytes.ingester.tasks;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.util.ExternalProcess;
import lombok.extern.slf4j.Slf4j;

/**
 * Allow zip uploads
 * @author kinderk
 */
@Component
@Slf4j
public class HandleZipFiles {
    @Autowired
    private FileSystemStorageService storageService;
    
    @Autowired
    private ExternalProcess externalProcess;

    @Scheduled(initialDelay = 1000, fixedRate = 1000)
    public void checkForZips()  {
        log.info("Checking for zip uploads");
        storageService.loadZipFromTemp().forEach(zipFile -> {
            // Send to unzip and wait
            try {
                externalProcess.unzip(zipFile.getFileName().toString());
            } catch (IOException | InterruptedException e) {
                log.error("Unable to unzip {}: {}", zipFile.toString(), e);                
            }
        });

        log.info("Done checking for zip uploads");
    }

    @Scheduled(initialDelay = 1000, fixedRate = 1000)
    public void checkForWavs()  {
        storageService.loadWavFromTemp().forEach(wavFile -> {
            log.info("FilePathing: {}",wavFile.toAbsolutePath());
            log.info("FileName: {}",wavFile.getFileName());
            // // Store in rootLoc
            // storageService.store(wavFile.toFile());

            // // Remove file
            // try {
            //     FileSystemUtils.deleteRecursively(wavFile);
            // } catch (IOException e) {
            //     log.error("Unable to remove {}, recursion may occur!", wavFile);
            // }
        });
        
        log.info("Done checking for WAV in temp");
    }
}
