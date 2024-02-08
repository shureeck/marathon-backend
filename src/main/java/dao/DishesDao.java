package dao;

import lombok.Setter;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DishesDao extends PostgreDaoAbstract {
    @Setter
    private int id = -1;

    @Override
    public String get() {
        String query = id < 0
                ? "select * from marathon.dishes;"
                : "select * from marathon.dishes where id = ?;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            if (id > 0) {
                statement.setInt(1, id);
            }
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

    public String update(String tittle) {
        String query = "UPDATE marathon.dishes SET tittle = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tittle);
            statement.setInt(2, id);
            int num = statement.executeUpdate();
            if (num == 1) {
                return "";
            } else if (num == 0) {
                return String.format("Заголовок рецепту %s не був збережений", tittle);
            } else {
                return String.format("%d заголовків рецептів було оновлено замість 1", num);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new IllegalStateException(exception.getMessage());
        }
    }
}
