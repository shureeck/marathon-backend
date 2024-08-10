package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
@NoArgsConstructor
@Getter
@Setter
public class ShareRequestEntity {
    @JsonProperty("marathonId")
    private String marathonId;
    @JsonProperty("users")
    private ArrayList<String> users;
}
