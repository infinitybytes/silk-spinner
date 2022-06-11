package ai.ibytes.ingester.storage.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ai.ibytes.ingester.util.Status;
import ai.ibytes.ingester.util.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Builder
public class DataFile {
    @JsonIgnore
    private String id;

    private String path;

    private String name;

    private byte[] audioBytes;

    private byte[] waveFormBytes;

    @Builder.Default
    private boolean voiceDetected = false;

    @Builder.Default
    private Status status = Status.NEW;

    @Builder.Default @JsonIgnore
    private List<Tags> tags = new ArrayList<Tags>();

    @Builder.Default
    private List<Long> voiceDetectTimes = new ArrayList<>();

    private RMSAnalysis rmsAnalysis;

    private PeakAnalysis peakAnalysis;

    public boolean getVoiceDetected()    {
        voiceDetected = voiceDetectTimes.size() > 0;
        return voiceDetected;
    }

    public boolean isVoiceDetected()    {
        voiceDetected = voiceDetectTimes.size() > 0;
        return voiceDetected;
    }

    public String getId()   {
        return( UUID.nameUUIDFromBytes( (path+name).getBytes() ).toString() );
    }
}
