package ai.ibytes.ingester.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.yaml.snakeyaml.util.UriEncoder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataFile {
    public enum STATUS {UPLOADED, CONVERTED, NEEDS_VAD, ANALYZED, ARCHIVED}
    private FTPFile rawFile;
    private String slug;
    private DataFile.STATUS status = DataFile.STATUS.UPLOADED;
    private Path localTempFile;

    private boolean voiceDetected = false;

    private List<String> voiceDetectTimes = new ArrayList<>();

    public String getSlug() {
        return UriEncoder.encode(this.slug);
    }

    public String getName() {
        return rawFile.getName();
    }

    public boolean isDirectory()    {
        return rawFile.isDirectory();
    }
}
