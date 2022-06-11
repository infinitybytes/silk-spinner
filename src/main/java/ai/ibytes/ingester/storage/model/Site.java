package ai.ibytes.ingester.storage.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ai.ibytes.ingester.util.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Site {
    @JsonIgnore
    private String id;

    @JsonIgnore
    private String siteJsonPath;

    @JsonIgnore
    private String siteCsvFilename;
    
    @JsonIgnore
    private String siteCsvRMSFilename;

    @JsonIgnore
    private String siteCsvPEAKFilename;

    private String name;
    private String season;
    private String dataLocation;
    private long numSourceFiles;

    @JsonIgnore
    private long numAnalyzedFiles;

    private boolean runningAnalysis;

    @Builder.Default
    private List<DataFile> dataFiles = new ArrayList<DataFile>();

    public String getId()   {
        return( UUID.nameUUIDFromBytes( (name+season).getBytes() ).toString() );
    }

    public long getNumAnalyzedFiles()   {
        return( dataFiles.stream().filter(df -> df.getStatus().equals(Status.ANALYZED)).count() );
    }

    public String getSiteJsonPath()   {
        return( new StringBuffer(getId()).append(".json").toString() );
    }

    public String getSiteCsvFilename()  {
        return( new StringBuffer(season).append("_").append(name).append(".csv").toString() );
    }

    public String getSiteCsvRMSFilename()  {
        return( new StringBuffer(season).append("_").append(name).append("_RMS.csv").toString() );
    }

    public String getSiteCsvPEAKFilename()  {
        return( new StringBuffer(season).append("_").append(name).append("_PEAK.csv").toString() );
    }
}
