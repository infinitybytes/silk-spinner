package ai.ibytes.ingester.storage.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    private String name;
    private String season;
    private String dataLocation;
    private long numSourceFiles;
    private long numAnalyzedFiles;

    @Builder.Default
    private List<DataFile> dataFiles = new ArrayList<DataFile>();

    public String getId()   {
        return( UUID.nameUUIDFromBytes( (name+season).getBytes() ).toString() );
    }
}
