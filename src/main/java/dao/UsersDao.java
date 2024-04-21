package dao;

import lombok.Builder;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


@Builder
public class UsersDao extends PostgreDaoAbstract {
    private static final String USER_QUERY = "select u.id, u.username, u.password, u.lastname, u.firstname, r.role " +
            "from marathon.marathon.users u " +
            "join marathon.marathon.roles r on u.\"role\" = r.id " +
            "where u.username = ? and u.password = ?";
    private static final String ALL_QUERY = "select u.id, u.username, u.password, u.lastname, u.firstname, r.role " +
            "from marathon.marathon.users u " +
            "join marathon.marathon.roles r on u.\"role\" = r.id ";
    private static final String BY_MARATHON_ID = "select * from marathon.marathon.users u  " +
            "join marathon.marathon.marathon_assign ma on ma.user_id = u.id " +
            "where  ma.marathon_id = ?;";

    private String userName;
    private String password;
    private String marathonId;

    @Override
    public String get() {
        try (Connection connection = getConnection();
             PreparedStatement statement = selectQuery(connection)) {
            ResultSet resultSet = statement.executeQuery();
            if (Objects.isNull(resultSet)) {
                return null;
            } else {
                ArrayList<HashMap<String, String>> list = new ArrayList<>();
                while (resultSet.next()) {
                    HashMap<String, String> result = new HashMap<>();
                    result.put("username", resultSet.getString("username"));
                    result.put("lastname", resultSet.getString("lastname"));
                    result.put("firstname", resultSet.getString("firstname"));
                    result.put("role", resultSet.getString("role"));
                    result.put("id", resultSet.getString("id"));
                    list.add(result);
                }
                return Utils.objectToJson(list);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T put(String string) {
        return null;
    }

    private PreparedStatement selectQuery(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(ALL_QUERY);
        if (Objects.nonNull(userName) && Objects.nonNull(password)) {
            statement = connection.prepareStatement(USER_QUERY);
            statement.setString(1, userName);
            statement.setString(2, password);
            return statement;
        } else if (Objects.nonNull(marathonId)) {
            statement = connection.prepareStatement(BY_MARATHON_ID);
            statement.setInt(1, Integer.parseInt(marathonId));
            return statement;
        }
        return statement;
    }
}
