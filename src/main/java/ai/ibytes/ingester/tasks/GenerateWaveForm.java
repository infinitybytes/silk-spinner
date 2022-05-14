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

@Slf4j
@Service
public class GenerateWaveForm {
    @Autowired
    private AppConfig appConfig;
    
    public void generate(DataFile dataFile)    {
        File tmpOutput = new File("/tmp", UUID.randomUUID().toString());

        try {
            Process proc = new ProcessBuilder(
                    "sh",
                    appConfig.getRootDir()+"/generate-wave-form.sh", 
                    dataFile.getPath(), 
                    tmpOutput.getAbsolutePath() )
                .inheritIO()
                .redirectOutput(Redirect.DISCARD)
            .start();
            
            proc.waitFor();
            
            dataFile.setWaveFormBytes(Files.readAllBytes(tmpOutput.toPath()));
        } catch (IOException | InterruptedException e) {
            log.error("{}: Error generating waveform", dataFile.getId(), e);
        } finally {
            tmpOutput.delete();
        }
    }
}
