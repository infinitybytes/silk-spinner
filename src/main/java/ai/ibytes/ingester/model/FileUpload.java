package ai.ibytes.ingester.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUpload {
    private String id;
    private String filename;
    private String status;
    private boolean waveform;
}
