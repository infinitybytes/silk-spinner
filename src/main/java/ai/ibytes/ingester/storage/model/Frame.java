package ai.ibytes.ingester.storage.model;

import java.util.Map;

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
    private Map<String, String> tags;
}
