package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

@NoArgsConstructor
public class GraficEntity {
    @Getter
    @Setter
    @JsonProperty("name")
    private String name;
    @Getter
    @Setter
    @JsonProperty("time")
    private String time;
    @Getter
    @Setter
    @JsonProperty("food")
    private ArrayList<Map<String, String>> food = new ArrayList<>();

    public GraficEntity(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public void addFood(Map<String, String> food) {
        this.food.add(food);
    }
}
