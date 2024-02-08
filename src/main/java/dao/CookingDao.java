package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CookingDao extends PostgreDaoAbstract {
    private int id;

    public CookingDao(int id) {
        this.id = id;
    }

    @Override
    public String get() {
        String query = "select description from marathon.cooking where dish_id = ?;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            } else {
                return null;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public String put(String string) {
        String query = "insert into  marathon.cooking (dish_id, description) values(?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, string);
            System.out.println("DAO:" + string);
            statement.executeQuery();

            return null;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public String update(String string) {
        String query = "UPDATE marathon.cooking SET description = ? WHERE dish_id = ?;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, string);
            statement.setInt(2, id);
            int num = statement.executeUpdate();
            if (num == 1) {
                return "";
            } else if (num == 0) {
                return String.format("Рецепту %d не був збережений", id);
            } else {
                return String.format("%d рецептів було оновлено замість 1", num);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
