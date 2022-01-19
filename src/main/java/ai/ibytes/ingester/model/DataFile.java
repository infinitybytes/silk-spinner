package ai.ibytes.ingester.model;

import org.apache.commons.net.ftp.FTPFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DataFile {
    private FTPFile rawFile;
    private String slug;

    public String getName() {
        return rawFile.getName();
    }
}
