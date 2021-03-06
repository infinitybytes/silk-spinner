package ai.ibytes.ingester.tasks;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.config.AppConfig;
import ai.ibytes.ingester.storage.model.DataFile;
import ai.ibytes.ingester.storage.model.PeakAnalysis;
import ai.ibytes.ingester.storage.model.RMSAnalysis;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenerateAudioStats {
    @Autowired
    private AppConfig appConfig;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    public void generate(DataFile dataFile)    {
        File tmpOutput = new File("/tmp", UUID.randomUUID().toString());
        File tmpBytes = new File(tmpOutput, "audio.wav");
        File peakOutput = new File(tmpOutput, "peak.json");
        File rmsOutput = new File(tmpOutput, "rms.json");

        try {
            Files.createDirectories(tmpOutput.toPath());
            Files.write(tmpBytes.toPath(), dataFile.getAudioBytes());

            Process proc = new ProcessBuilder(
                    "sh",
                    appConfig.getRootDir()+"/generate-audio-stats.sh", 
                    tmpBytes.getAbsolutePath(), 
                    tmpOutput.getAbsolutePath() )
                .inheritIO()
                .redirectOutput(Redirect.DISCARD)
            .start();
            
            proc.waitFor();
            
            // tmpOutput .peak.json, .rms.json
            dataFile.setPeakAnalysis(objectMapper.readValue( peakOutput, PeakAnalysis.class));
            dataFile.setRmsAnalysis(objectMapper.readValue( rmsOutput, RMSAnalysis.class));

        } catch (IOException | InterruptedException e) {
            log.error("{}: Error generating audio stats", dataFile.getId(), e);
        } finally {
            peakOutput.delete();
            rmsOutput.delete();
            tmpBytes.delete();
            tmpOutput.delete();
        }
    }
}
