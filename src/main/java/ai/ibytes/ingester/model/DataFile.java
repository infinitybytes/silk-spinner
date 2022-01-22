package ai.ibytes.ingester.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class DataFile {
    public enum STATUS {UPLOADED, CONVERTED, NEEDS_VAD, ANALYZED, ARCHIVED}
    private String dirName;
    private DataFile.STATUS status = DataFile.STATUS.UPLOADED;
    private Path localTempFile;
    private String name;

    private boolean voiceDetected = false;

    private List<String> voiceDetectTimes = new ArrayList<>();
}
