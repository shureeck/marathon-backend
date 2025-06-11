package entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIResponse {
    @NoArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)

    private static class Choices{
        @JsonProperty("message")
        private HashMap<String, Object> message;
    }

    @JsonProperty("choices")
    private List<Choices> choices;

    public String getMessage(){
        return  choices.get(0).message.get("content").toString();
    };
}
