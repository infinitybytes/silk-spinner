package ai.ibytes.ingester.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

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
    private FTPFile rawFile;
    private String slug;
    private DataFile.STATUS status = DataFile.STATUS.UPLOADED;
    private Path localTempFile;
    private String name;

    private boolean voiceDetected = false;

    private List<String> voiceDetectTimes = new ArrayList<>();

    public String getSlug() {
        return this.slug;
    }

    public String getName() {
        if(rawFile!=null)   {
            return rawFile.getName();
        }
        return this.name;
    }

    public boolean isDirectory()    {
        return rawFile.isDirectory();
    }
}
