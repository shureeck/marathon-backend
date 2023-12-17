package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;

@NoArgsConstructor
public class WeekEntity {
    @Getter
    @Setter
    @JsonProperty("week")
    private String week;
    @Getter
    @Setter
    @JsonProperty("days")
    private ArrayList<DayEntity> days = new ArrayList<>();

    public WeekEntity(String week) {
        this.week = week;
    }

    public void addDay(DayEntity day) {
        DayEntity dayEntity = days.stream().filter(p -> (p.getDay().equals(day.getDay())))
                .findFirst().orElse(null);
        if (Objects.isNull(dayEntity)) {
            days.add(day);
        } else {
            dayEntity.addGrafic(day.getGrafic().get(0));
        }
    }
}
