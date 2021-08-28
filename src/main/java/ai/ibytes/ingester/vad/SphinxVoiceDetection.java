package ai.ibytes.ingester.vad;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SphinxVoiceDetection {
    @Autowired
    private FileSystemStorageService storageService;

    private Configuration configuration = new Configuration();
    private StreamSpeechRecognizer recognizer;
    
    public SphinxVoiceDetection()   {
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        configuration.setSampleRate(16000);
        try {
            recognizer = new StreamSpeechRecognizer(configuration);
        } catch (IOException e) {
            log.error("Unable to init the speech recognizer");
        }
    }

    /**
     * Try to detect voice in the audio file
     * @param fileUpload
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<String> detectVoice(FileUpload fileUpload) throws FileNotFoundException, IOException  {
        List<String> detectedSpeech = new ArrayList<>();
        recognizer.startRecognition(new FileInputStream(storageService.loadAsResource(fileUpload.getFilename()).getFile()));
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            for (WordResult r : result.getWords()) {
                detectedSpeech.add(r.getTimeFrame().toString());
            }
        }
        recognizer.stopRecognition();
        return detectedSpeech;
    }
}
