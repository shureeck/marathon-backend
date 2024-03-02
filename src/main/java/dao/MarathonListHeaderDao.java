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
public class MarathonListHeaderDao extends PostgreDaoAbstract {
    private int marathonId = -1;
    private String userRole;
    private int userId;

    @Override
    public String get() {
        String query;
        if (marathonId <= 0) {
            if (Objects.nonNull(userRole) && userRole.equalsIgnoreCase("admin")) {
                query = "select name from marathon.marathon_list where is_active=true";
            } else {
                query = "select ml.name from marathon.marathon_list ml "
                        + "inner join marathon.marathon_assign ma "
                        + "on ma.user_id = ? and ma.is_active = true and ml.marathon_id = ma.marathon_id;";
            }
        } else {
            query = "select name from marathon.marathon_list where marathon_id = ?";
        }
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            if (marathonId <= 0) {
                statement.setInt(1, userId);
            } else {
                statement.setInt(1, marathonId);
            }
            ResultSet resultSet = statement.executeQuery();
            if (Objects.isNull(resultSet)) {
                return null;
            }
            ArrayList<HashMap<String, Object>> result = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<String, Object> map = new HashMap();
                map.put("name", resultSet.getString("name"));
                result.add(map);
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
