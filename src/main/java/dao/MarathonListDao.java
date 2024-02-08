package dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Builder
public class MarathonListDao extends PostgreDaoAbstract {
    private static final String INSERT = "INSERT INTO marathon.marathon_list " +
            "(name, description, owner, is_active) " +
            "VALUES (?, ?, ?,false);";
    private static final String SELECT_FOR_DISH = "select distinct ml.name from marathon.marathon m " +
            "join marathon.marathon_list ml  on m.marathon_id = ml.marathon_id " +
            "where  dishes_id = ?";

    private int userID = -1;
    private int dishID = -1;

    @Override
    public String get() {
        String query = userID == 0
                ? "select * from marathon.marathon_list m order by m.marathon_id;"
                : "select m.marathon_id,  m.name, a.is_active from marathon.marathon_list m "
                + "join marathon.marathon_assign a on a.marathon_id = m.marathon_id "
                + "where a.user_id = ?;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            if (dishID > 0) {
                return getMarafonListForDish(connection);
            }
            if (userID > 0) {
                statement.setInt(1, userID);
            }
            ResultSet resultSet = statement.executeQuery();
            if (Objects.isNull(resultSet)) {
                return null;
            }
            ArrayList<HashMap<String, Object>> result = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<String, Object> map = new HashMap();
                map.put("id", resultSet.getString("marathon_id"));
                map.put("name", resultSet.getString("name"));
                if (userID < 0) {
                    map.put("description", resultSet.getString("description"));
                    map.put("owner", resultSet.getString("owner"));
                    map.put("created", resultSet.getString("created"));
                } else {
                    map.put("active", resultSet.getBoolean("is_active"));
                }
                result.add(map);
            }

            return Utils.objectToJson(result);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private String getMarafonListForDish(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_FOR_DISH)) {
            statement.setInt(1, dishID);
            ResultSet resultSet = statement.executeQuery();
            if (Objects.isNull(resultSet)) {
                return null;
            }
            ArrayList<String> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(resultSet.getString("name"));
            }
            return Utils.objectToJson(result);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }
    }

    @Override
    public String put(String string) {
        HashMap<String, String> body = Utils.jsonToObject(string, HashMap.class);
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, body.get("name"));
            statement.setString(2, body.get("description"));
            statement.setInt(3, userID);
            int result = statement.executeUpdate();
            if (result == 1) {
                return "";
            } else {
                return null;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }
    }
}
