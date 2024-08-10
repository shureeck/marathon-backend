package dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
public class MarathonAssignDao extends PostgreDaoAbstract {
    private static final String SELECT_USERS = "select user_id from marathon.marathon_assign where marathon_id = ?; ";
    private static final String INSERT_USERS = "insert  into marathon.marathon_assign (user_id, marathon_id) values (?, ?);";
    private static final String DELETE_USERS = "delete from  marathon.marathon_assign where  marathon_id = ? and  user_id in (%s);";
    private int marathonId;
    @Setter
    @Getter
    private List<Integer> users;

    @Override
    public ArrayList<String> get() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USERS)) {

            statement.setInt(1, marathonId);

            ResultSet resultSet = statement.executeQuery();
            if (Objects.isNull(resultSet)) {
                return null;
            }
            ArrayList<String> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(resultSet.getString("user_id"));
            }
            return result;

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public String put(String string) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USERS)) {

            for (int user : users) {
                statement.setInt(1, user);
                statement.setInt(2, marathonId);
                statement.addBatch();
            }

            statement.executeBatch();
            return "201";

        } catch (SQLException exception) {
            exception.printStackTrace();
            return "500";
        }
    }

    public String delete() {
        String usersIDs = users.stream().map(String::valueOf).collect(Collectors.joining(", "));
        String query = String.format(DELETE_USERS, usersIDs);
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, marathonId);
            statement.executeUpdate();
            return "201";
        } catch (SQLException exception) {
            exception.printStackTrace();
            return "500";
        }
    }
}
