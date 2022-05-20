package ai.ibytes.ingester.tasks;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.stereotype.Service;

import ai.ibytes.ingester.storage.model.DataFile;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DetectHumanVoice {
    private Configuration configuration = new Configuration();
    private StreamSpeechRecognizer recognizer;
    
    public DetectHumanVoice()   {
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
    public void detectVoice(DataFile dataFile) {
        recognizer.startRecognition(new ByteArrayInputStream(dataFile.getAudioBytes()));
        
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            for (WordResult r : result.getWords()) {
                dataFile.getVoiceDetectTimes().add(r.getTimeFrame().getStart());
            }
        }
        recognizer.stopRecognition();
    }
}
