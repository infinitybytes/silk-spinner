package ai.ibytes.ingester.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUpload {
    public enum STATUS {UPLOADED, CONVERTED, NEEDS_VAD, ANALYZED, ARCHIVED}

    private String id;
    private String filename;
    private FileUpload.STATUS status;
    private boolean waveform;
    private boolean voiceDetected;

    private List<String> voiceDetectTimes;
}
