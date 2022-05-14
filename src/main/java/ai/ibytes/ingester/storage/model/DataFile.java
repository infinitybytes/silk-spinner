package ai.ibytes.ingester.storage.model;

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
    private String id;
    private String filePath;
    private String fileName;

    private byte[] waveFormBytes;
    private boolean voiceDetected = false;

    private Status status = Status.NEW;
    private List<Tags> tags = new ArrayList<Tags>();
    private List<String> voiceDetectTimes = new ArrayList<>();
}
