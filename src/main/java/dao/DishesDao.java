package dao;

import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DishesDao extends PostgreDaoAbstract {

    @Override
    public String get() {
        String query = "select * from marathon.dishes;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            HashMap<String, String> map = new HashMap<>();
            while (rs.next()) {
                map.put(rs.getString(1), rs.getString(2));
            }
            return Utils.objectToJson(map);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public String put(String string) {
        String query = "insert into marathon.dishes (tittle) values(?)";
        String selectQuery = "select id from marathon.dishes where tittle=?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, string);
            statement.executeUpdate();

            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, string);
            ResultSet rs = selectStatement.executeQuery();
            rs.next();
            return rs.getString(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            if (Integer.valueOf(exception.getSQLState()) == 23505) {
                throw new IllegalStateException(String
                        .format("Рецепт для страви \"%s\" вже існує. Змініьть назву або використайте існуючий.",
                                string));
            } else {
                throw new IllegalStateException(exception.getMessage());
            }
        }
    }
}
