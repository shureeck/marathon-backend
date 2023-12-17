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
        String query = "select d.tittle, c.description from marathon.cooking c \n"
                + "join marathon.dishes d on d.id = c.dish_id and d.id = ?;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return "<h2>" + resultSet.getString(1) + "</h2>" + resultSet.getString(2);
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
}
