package ai.ibytes.ingester.storage.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Frame {
    private String pkt_pts_time;

    @JsonIgnore
    private Map<String, String> tags;

    @JsonIgnore
    private String decibal;

    public String getDecibal()  {
        return tags.values().stream().findFirst().get();
    }
}
