package dao;

import entities.DayEntity;
import entities.GraficEntity;
import entities.WeekEntity;
import enums.Days;
import lombok.Builder;
import utils.Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Builder
public class MarathonDao extends PostgreDaoAbstract {
    private int id = -1;
    private int userId;
    private String userRole;

    @Override
    public String get() {
        try (Connection connection = getConnection()) {
            PreparedStatement statement;
            if (id <= 0) {
                if (Objects.nonNull(userRole) && userRole.equalsIgnoreCase("admin")) {
                    statement = prepareAdminStatement(connection);
                } else {
                    statement = prepareUserStatement(connection);
                }
            } else {
                statement = prepareIdStatement(connection);
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
        String marathonId = null;
        while (rs.next()) {
            if (Objects.isNull(marathonId)) {
                marathonId = rs.getString("marathon_id");
            }
            String week = "Тиждень " + rs.getString("week_id");
            String day = getDay(rs.getInt("day_id"));
            String eatName = rs.getString("tittle");
            String time = rs.getString("timeset");

            String food = rs.getString("food");
            if (Objects.nonNull(rs.getString("value"))) {
                food = rs.getString("value") + " " + food;
            }
            String recipeId = rs.getString("id");

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
        HashMap<String, Object> result = new HashMap<>();
        result.put("marathonId", marathonId);
        result.put("weeks", weeks);
        return Utils.objectToJson(result);
    }

    private String getDay(int i) {
        return Days.getDay(i);
    }

    @Override
    public String put(String string) {
        System.out.println(string);
        String query = "insert into marathon.marathon " +
                "(week_id, day_id, schedule_id, dishes_id, value, marathon_id) " +
                "values (?, ?, ?, ?, ?, ?);";
        System.out.println(string);
        HashMap<String, Object> map = Utils.jsonToObject(string, HashMap.class);
        ArrayList<String> days = (ArrayList<String>) map.get("day");
        System.out.println(days.size());
        System.out.println(String.join("; ", days));
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            for (String day : days) {
                statement.setInt(1, Integer.parseInt((String) map.get("week")));
                statement.setInt(2, Integer.parseInt(day));
                statement.setInt(3, Integer.parseInt((String) map.get("sceduler")));
                statement.setInt(4, Integer.parseInt((String) map.get("food")));
                statement.setString(5, (String) map.get("quantity"));
                statement.setInt(6, Integer.parseInt((String) map.get("marathon_id")));
                statement.addBatch();
            }
            statement.executeBatch();
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

    public String delete(HashMap<String, String> parameters) {
        String selectQuery = "select m.id from marathon.marathon m "
                + "inner join marathon.marathon_list ml on ml.\"name\"=? and m.marathon_id = ml.marathon_id "
                + "inner join marathon.schedule s  on s.tittle=? and s.timeset=? and m.schedule_id = s.id "
                + "where m.week_id=? and m.day_id=? and m.dishes_id=?;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(selectQuery)) {

            statement.setString(1, parameters.get("marathon"));
            statement.setString(2, parameters.get("schedule"));
            statement.setString(3, parameters.get("time"));
            statement.setInt(4, Integer.parseInt(parameters.get("week").split(" ")[1].trim()));
            statement.setInt(5, Days.getDayId(parameters.get("day").trim()));
            statement.setInt(6, Integer.parseInt(parameters.get("food")));
            ResultSet rs = statement.executeQuery();
            ArrayList<Integer> idList = new ArrayList<>();
            while (rs.next()) {
                idList.add(rs.getInt("id"));
            }

            if (idList.size() == 1) {
                PreparedStatement statementDelete = connection.prepareStatement("delete from marathon.marathon where id=?;");
                statementDelete.setInt(1, idList.get(0));
                int num = statementDelete.executeUpdate();
                if (num == 1) {
                    return "";
                } else {
                    return "Server Error";
                }
            } else if (idList.size() > 1) {
                return "Знайдено кілька співпадінь по запросу. Нічого не видалено";
            } else {
                return "Страву не знайдено. Видалення не успішне";
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }
    }

    private PreparedStatement prepareAdminStatement(Connection connection) throws SQLException {
        String adminQuery = "select m.marathon_id, m.week_id, m.day_id, s.tittle, s.timeset, d.tittle food, m.value, c.dish_id id from marathon.marathon m "
                + "inner join marathon.marathon_list ml on ml.is_active = true and ml.marathon_id=m.marathon_id "
                + "inner join marathon.schedule s on m.schedule_id = s.id "
                + "inner join marathon.dishes d on m.dishes_id = d.id "
                + "inner join marathon.cooking c on c.dish_id = d.id "
                + "order by m.week_id, m.day_id, m.schedule_id;";
        PreparedStatement statement = connection.prepareStatement(adminQuery);
        return statement;
    }

    private PreparedStatement prepareUserStatement(Connection connection) throws SQLException {
        String userQuery = "select m.marathon_id, m.week_id, m.day_id, s.tittle, s.timeset, d.tittle food, m.value, c.dish_id id from marathon.marathon m "
                + "inner join marathon.marathon.marathon_assign ma on ma.is_active = true and ma.marathon_id = m.marathon_id and ma.user_id = ?"
                + "inner join marathon.schedule s on m.schedule_id = s.id "
                + "inner join marathon.dishes d on m.dishes_id = d.id "
                + "inner join marathon.cooking c on c.dish_id = d.id "
                + "order by m.week_id, m.day_id, m.schedule_id;";
        PreparedStatement statement = connection.prepareStatement(userQuery);
        statement.setInt(1, userId);
        return statement;
    }

    private PreparedStatement prepareIdStatement(Connection connection) throws SQLException {
        String marathonQuery = "select m.marathon_id, m.week_id, m.day_id, s.tittle, s.timeset, d.tittle food, m.value, c.dish_id id from marathon.marathon m "
                + "inner join marathon.schedule s on m.schedule_id = s.id "
                + "inner join marathon.dishes d on m.dishes_id = d.id "
                + "inner join marathon.cooking c on c.dish_id = d.id "
                + "where m.marathon_id = ? "
                + "order by m.week_id, m.day_id, m.schedule_id;";
        PreparedStatement statement = connection.prepareStatement(marathonQuery);
        statement.setInt(1, id);
        return statement;
    }
}
