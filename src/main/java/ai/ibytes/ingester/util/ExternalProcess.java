package ai.ibytes.ingester.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.ibytes.ingester.config.AppConfig;
import ai.ibytes.ingester.config.StorageConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Launches external scripts and commands
 * @author kinderk
 */
@Component
@Slf4j
public class ExternalProcess {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private StorageConfig storageConfig;

    public int convertBitrate(String filename) throws IOException, InterruptedException   {
        Path pathToFile = (isWindows) ? Path.of("C:\\", storageConfig.getTempStore(), filename) : Path.of(storageConfig.getTempStore(), filename);

        log.info("Running external conversion process on {}",pathToFile.toString());

        ProcessBuilder builder = new ProcessBuilder().inheritIO();
        if (isWindows) {
            builder.command("cmd.exe", "/c", "convert-32b-to-16b-mono.bat", pathToFile.toString());
        } else {
            builder.command("sh", "-c", "convert-32b-to-16b-mono.sh", pathToFile.toString());
        }
        builder.directory(new File(appConfig.getRootDir(), "bin"));

        // Kick off the process and monitor
        Process process = builder.start();
        BufferedOutputStream br = new BufferedOutputStream(process.getOutputStream());
        log.info(br.toString());  

        // Clean exit?
        return process.waitFor();
    }

    public int generateWaveform(String filename) throws IOException, InterruptedException   {
        Path pathToFile = (isWindows) ? Path.of("C:\\", storageConfig.getTempStore(), filename) : Path.of(storageConfig.getTempStore(), filename);

        log.info("Running external waveform process on {}",pathToFile.toString());

        ProcessBuilder builder = new ProcessBuilder().inheritIO();
        if (isWindows) {
            builder.command("cmd.exe", "/c", "generate-wave-form.bat", pathToFile.toString());
        } else {
            builder.command("sh", "-c", "generate-wave-form.sh", pathToFile.toString());
        }
        builder.directory(new File(appConfig.getRootDir(), "bin"));

        // Kick off the process and monitor
        Process process = builder.start();
        BufferedOutputStream br = new BufferedOutputStream(process.getOutputStream());
        log.info(br.toString());  

        // Clean exit?
        return process.waitFor();
    }
}
