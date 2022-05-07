package ai.ibytes.ingester.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ai.ibytes.ingester.util.Status;
import ai.ibytes.ingester.util.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class DataFile {
    private Status status = Status.NEW;
    private List<Tags> tags = new ArrayList<Tags>();
    
    private String id;
    private String filePath;
    private String fileName;
    private Path audioFile;

    private byte[] waveFormBytes;

    private boolean voiceDetected = false;

    private List<String> voiceDetectTimes = new ArrayList<>();
}
