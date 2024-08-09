package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

@NoArgsConstructor
@Setter
@Getter
public class GraficEntity {
    @JsonProperty("name")
    private String name;
    @JsonProperty("time")
    private String time;
    @JsonProperty("food")
    private ArrayList<FoodEntity> food = new ArrayList<>();

    public GraficEntity(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public void addFood(FoodEntity food) {
        this.food.add(food);
    }
}
