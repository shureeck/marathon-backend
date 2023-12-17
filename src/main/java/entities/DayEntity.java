package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;

@NoArgsConstructor
public class DayEntity {
    @Getter
    @Setter
    @JsonProperty("day")
    private String day;
    @Getter
    @Setter
    @JsonProperty("grafic")
    private ArrayList<GraficEntity> grafic = new ArrayList<>();

    public DayEntity(String day) {
        this.day = day;
    }

    public void addGrafic(GraficEntity graficEntity) {
        GraficEntity entity = grafic.stream()
                .filter(p -> (p.getName().equals(graficEntity.getName()) &&
                        p.getTime().equals(graficEntity.getTime())))
                .findFirst().orElse(null);
        if (Objects.isNull(entity)) {
            grafic.add(graficEntity);
        } else {
            entity.addFood(graficEntity.getFood().get(0));
        }

    }
}
