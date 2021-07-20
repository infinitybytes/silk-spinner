package ai.ibytes.ingester.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUpload {
    private String id;
    private String filename;
    private String status;
}
