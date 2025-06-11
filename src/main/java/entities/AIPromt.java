package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class AIPromt {
    @JsonProperty("model")
    private String model ="gpt-4o-mini";
    @JsonProperty("store")
    private boolean store = true;
    @JsonProperty("messages")
    private List<HashMap<String, String>> messages= List.of(new HashMap(Map.of("role", "user")));

    public  void setMessage(String text){
        this.messages.get(0).put("content", text);
    }
}
