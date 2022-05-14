package ai.ibytes.ingester.storage.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RMSAnalysis {
    private List<Frame> frames;
}
