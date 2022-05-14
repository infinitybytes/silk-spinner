package ai.ibytes.ingester.storage.model;

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

    public String getId()   {
        return( UUID.nameUUIDFromBytes( (name+season).getBytes() ).toString() );
    }
}
