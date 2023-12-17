package dao;

import entities.DayEntity;
import entities.GraficEntity;
import entities.WeekEntity;
import lombok.Setter;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MarathonDao extends PostgreDaoAbstract {
    @Setter
    private int id=-1;

    @Override
    public String get() {
        String query = "select m.week_id, m.day_id, s.tittle, s.timeset, d.tittle, m.value, c.dish_id id from marathon.marathon m "
                + "inner join marathon.marathon_list ml on ml.is_active = true and ml.marathon_id=m.marathon_id "
                + "inner join marathon.schedule s on m.schedule_id = s.id "
                + "inner join marathon.dishes d on m.dishes_id = d.id "
                + "inner join marathon.cooking c on c.dish_id = d.id "
                + "order by m.week_id, m.day_id, m.schedule_id;";
        if(id>0){
            query = "select m.week_id, m.day_id, s.tittle, s.timeset, d.tittle, m.value, c.dish_id id from marathon.marathon m "
                    + "inner join marathon.schedule s on m.schedule_id = s.id "
                    + "inner join marathon.dishes d on m.dishes_id = d.id "
                    + "inner join marathon.cooking c on c.dish_id = d.id "
                    + "where m.marathon_id = ? "
                    + "order by m.week_id, m.day_id, m.schedule_id;";
        }


        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            if(id>0){
                statement.setInt(1, id);
            }
            ResultSet resultSet = statement.executeQuery();
            if (Objects.isNull(resultSet)) {
                return null;
            }
            return prosesResultSet(resultSet);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private String prosesResultSet(ResultSet rs) throws SQLException {
        ArrayList<WeekEntity> weeks = new ArrayList<>();
        while (rs.next()) {
            String week = "Тиждень " + rs.getString(1);
            String day = getDay(rs.getInt(2));
            String eatName = rs.getString(3);
            String time = rs.getString(4);

            String food = rs.getString(5);
            if (Objects.nonNull(rs.getString(6))) {
                food = rs.getString(6) + " " + food;
            }
            String recipeId = rs.getString(7);

            Map<String, String> foodMap = Map.of(food, recipeId);

            GraficEntity graficEntity = new GraficEntity(eatName, time);
            graficEntity.addFood(foodMap);

            DayEntity dayEntity = new DayEntity(day);
            dayEntity.addGrafic(graficEntity);

            WeekEntity weekEntity = new WeekEntity(week);
            weekEntity.addDay(dayEntity);
            WeekEntity entity = weeks.stream().filter(p -> (p.getWeek().equals(weekEntity.getWeek())))
                    .findFirst().orElse(null);
            if (Objects.isNull(entity)) {
                weeks.add(weekEntity);
            } else {
                entity.addDay(weekEntity.getDays().get(0));
            }
        }
        return Utils.objectToJson(weeks);
    }

    private String getDay(int i) {
        switch (i) {
            case 1:
                return "Понеділок";
            case 2:
                return "Вівторок";
            case 3:
                return "Середа";
            case 4:
                return "Четвер";
            case 5:
                return "П'ятниця";
            case 6:
                return "Субота";
            case 7:
                return "Неділя";
            default:
                return "UNKNOWN";
        }
    }

    @Override
    public String put(String string) {
        System.out.println(string);
        String query = "insert into marathon.marathon " +
                "(week_id, day_id, schedule_id, dishes_id, value, marathon_id) " +
                "values (?, ?, ?, ?, ?, ?);";
        HashMap<String, String> map = Utils.jsonToObject(string, HashMap.class);
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(map.get("week")));
            statement.setInt(2, Integer.parseInt(map.get("day")));
            statement.setInt(3, Integer.parseInt(map.get("sceduler")));
            statement.setInt(4, Integer.parseInt(map.get("food")));
            statement.setString(5, map.get("quantity"));
            statement.setInt(6, Integer.parseInt(map.get("marathon_id")));
            statement.executeUpdate();
            return null;
        } catch (SQLException exception) {
            exception.printStackTrace();
            if (Integer.valueOf(exception.getSQLState()) == 23505) {
                throw new IllegalStateException(String
                        .format("Ця страва вже була додана на цей прийом їжі.",
                                string));
            } else {
                throw new IllegalStateException(exception.getMessage());
            }
        }
    }
}
