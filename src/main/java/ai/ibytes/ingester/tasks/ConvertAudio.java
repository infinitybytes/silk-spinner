package ai.ibytes.ingester.tasks;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.config.AppConfig;
import ai.ibytes.ingester.storage.model.DataFile;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConvertAudio {
    @Autowired
    private AppConfig appConfig;

    public void convert(DataFile dataFile)  {
        File tmpOutput = new File("/tmp", UUID.randomUUID().toString());
        File convertedOutput = new File(tmpOutput, "converted.wav");

        try {
            Files.createDirectories(tmpOutput.toPath());
            
            Process proc = new ProcessBuilder(
                    "sh",
                    appConfig.getRootDir()+"/convert-32b-to-16b-mono.sh", 
                    dataFile.getPath(), 
                    convertedOutput.getAbsolutePath() )
                .inheritIO()
                .redirectOutput(Redirect.DISCARD)
            .start();
            
            proc.waitFor();

            // save converted bytes to object temporarily
            dataFile.setAudioBytes(Files.readAllBytes(convertedOutput.toPath()));

        } catch (IOException | InterruptedException e) {
            log.error("{}: Error converting audio", dataFile.getId(), e);
        } finally {
            convertedOutput.delete();
            tmpOutput.delete();
        }
    }
}
