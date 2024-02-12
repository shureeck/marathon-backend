package enums;

import lombok.Getter;

import java.util.Arrays;

public enum Days {
    monday(1, "Понеділок"),
    tuesday(2, "Вівторок"),
    wednesday(3, "Середа"),
    thursday(4, "Четвер"),
    friday(5, "П'ятниця"),
    saturday(6, "Субота"),
    sunday(7, "Неділя"),
    unknown(0, "UNKNOWN");

    @Getter
    private int id;
    @Getter
    private String title;

    Days(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static String getDay(int i) {
        return Arrays.stream(values())
                .filter((p) -> (p.getId() == i))
                .findFirst().orElse(unknown).getTitle();
    }

    public static int getDayId(String day) {
        return Arrays.stream(values())
                .filter((p) -> (p.getTitle().equalsIgnoreCase(day)))
                .findFirst().orElse(unknown).getId();
    }
}


