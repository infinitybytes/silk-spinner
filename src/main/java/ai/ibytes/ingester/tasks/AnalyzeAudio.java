package ai.ibytes.ingester.tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.ibytes.ingester.model.DataFile;
import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.util.ExternalProcess;
import ai.ibytes.ingester.util.FtpClient;
import ai.ibytes.ingester.vad.SphinxVoiceDetection;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled tasks to generate a waveform and stats
 * @author kinderk
 */
@Component
@Slf4j
public class AnalyzeAudio implements Runnable {

    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private ExternalProcess externalProcess;

    @Autowired
    private SphinxVoiceDetection sphinxVoiceDetection;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    private DataFile dataFile;

    public void setDataFile(DataFile dataFile)  {
        this.dataFile = dataFile;
    }

    public void convertAudioRate()  {
        log.info("Converting sample rates and channel");
        
        try {
            FileUpload f = (FileUpload)objectMapper.readValue(storageService.loadJson(dataFile.getName()).toFile(), FileUpload.class);

            if(f.getStatus().equals(FileUpload.STATUS.UPLOADED))   {
                log.info( "{} needs a waveform, generating", f.getFilename());
                externalProcess.convertBitrate(f.getFilename());
                f.setStatus(FileUpload.STATUS.CONVERTED);
                storageService.save(f);
                log.info( "Finished converting {}", f.getFilename());
            }

        } catch (IOException | InterruptedException e) {
            log.error("Unable to convert bitrate for {}", dataFile.getLocalTempFile().toAbsolutePath());
        }
        
        log.info("Finished converting bitrate");
    }

    public void generateWaveform()  {
        log.info("Generating waveforms");

        try {
            FileUpload f = (FileUpload)objectMapper.readValue(storageService.loadJson(dataFile.getName()).toFile(), FileUpload.class);

            if(f.getStatus().equals(FileUpload.STATUS.CONVERTED))   {
                log.info( "{} needs a waveform, generating", f.getFilename());
                externalProcess.generateWaveform(f.getFilename());
                f.setWaveform(true);
                f.setStatus(FileUpload.STATUS.NEEDS_VAD);
                storageService.save(f);
                log.info( "Finished generating waveform for {}", f.getFilename());
            }

        } catch (IOException | InterruptedException e) {
            log.error("Unable to generate waveform for {}", dataFile.getName());
        }

        log.info("Finished generating waveforms");
    }

    public void detectVoice()   {
        log.info("Detecting voices");

        try {
            FileUpload f = (FileUpload)objectMapper.readValue(storageService.loadJson(dataFile.getName()).toFile(), FileUpload.class);

            if(f.getStatus().equals(FileUpload.STATUS.NEEDS_VAD))   {
                log.info( "{} needs VAD, generating", f.getFilename());
                List<String> vadResults = sphinxVoiceDetection.detectVoice(f);
                if(vadResults.size()!=0)    {
                    log.info("Speech detected in {}",f.getFilename());
                    f.setVoiceDetected(true);
                    f.setVoiceDetectTimes(vadResults);
                }
                f.setStatus(FileUpload.STATUS.ANALYZED);
                storageService.save(f);
                log.info( "Finished detecting voice for {}", f.getFilename());
            }

        } catch (IOException e) {
            log.error("Unable to detect voice for {}", dataFile.getName());
        }

        log.info("Finished detecting voice");
    }

    @Override
    public void run() {
        // Run converter, waveform, VAD
       // convertAudioRate();
       // generateWaveform();
       // detectVoice();

        // Finished, delete temp local file
        // try {
        //     Files.deleteIfExists(dataFile.getLocalTempFile());
        // } catch (IOException e) {
        //     log.error("Unable to delete temp local file",e);
        // }
    }
}
