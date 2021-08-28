package ai.ibytes.ingester.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ai.ibytes.ingester.storage.FileSystemStorageService;
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
    
    @Scheduled(initialDelay = 3000, fixedRate = 1000)
    public void checkForZips()  {
        log.info("Checking for zip uploads");

        log.info("Done checking for zip uploads");
    }
}
