package dao;

import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SceduleDao extends PostgreDaoAbstract {

    @Override
    public String get() {
        String query = "select * from marathon.schedule;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            HashMap<String, String> result = new HashMap<>();
            while (resultSet.next()) {
                result.put(resultSet.getString(1),
                        resultSet.getString(3)+" "+ resultSet.getString(2));
            }
            return Utils.objectToJson(result);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T put(String string) {
        return null;
    }
}
