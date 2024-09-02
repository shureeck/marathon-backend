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
    private static final String ALL_QUERY = "select u.id, u.username, u.password, u.lastname, u.firstname, r.role, ml.\"name\" as marathon_name " +
            "from marathon.marathon.users u " +
            "join marathon.marathon.roles r on u.\"role\" = r.id " +
            "LEFT JOIN marathon.marathon_assign ma  ON ma.user_id = u.id " +
            "LEFT JOIN marathon.marathon_list ml ON ml.marathon_id = ma.marathon_id " +
            "where r.id <> 1";
    private static final String BY_MARATHON_ID = "select * from marathon.marathon.users u  " +
            "join marathon.marathon.marathon_assign ma on ma.user_id = u.id " +
            "where  ma.marathon_id = ?;";

    private static final String INSERT = "insert into marathon.users " +
            "(username, password, lastname, firstname, role) " +
            "values (?, ?, ?, ?, ?);";

    private static final int ROLE = 3;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String marathonId;

    @Override
    public String get() {
        try (Connection connection = getConnection();
             PreparedStatement statement = selectQuery(connection)) {
            ResultSet resultSet = statement.executeQuery();
            if (Objects.isNull(resultSet)) {
                return null;
            } else {
                ArrayList<HashMap<String, Object>> list = new ArrayList<>();
                while (resultSet.next()) {
                    String userName = resultSet.getString("username");
                    HashMap<String, Object> user = list.stream()
                            .filter(p -> p.get("username").equals(userName))
                            .findAny().orElse(null);
                    if (Objects.isNull(user)) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("username", userName);
                        result.put("lastname", resultSet.getString("lastname"));
                        result.put("firstname", resultSet.getString("firstname"));
                        result.put("role", resultSet.getString("role"));
                        result.put("id", resultSet.getString("id"));
                        if (checkColumn(resultSet, "marathon_name")) {
                            ArrayList<String> arr = new ArrayList<>();
                            String marathonName = resultSet.getString("marathon_name");
                            if (Objects.nonNull(marathonName)) arr.add(marathonName);
                            result.put("marathonname", arr);
                        }
                        list.add(result);
                    } else {
                        ((ArrayList<String>) user.get("marathonname")).add(resultSet.getString("marathon_name"));
                    }
                }
                return Utils.objectToJson(list);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer put(String string) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, userName);
            statement.setString(2, password);
            statement.setString(3, lastName);
            statement.setString(4, firstName);
            statement.setInt(5, ROLE);
            int i = statement.executeUpdate();
            return i;
        } catch (SQLException exception) {
            exception.printStackTrace();

        }
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

    private boolean checkColumn(ResultSet resultSet, String column) {
        try {
            resultSet.findColumn(column);
            return true;
        } catch (SQLException sqlex) {
            return false;
        }
    }
}
