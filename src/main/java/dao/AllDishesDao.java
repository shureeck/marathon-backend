package dao;

import entities.FoodEntity;
import entities.GraficEntity;
import utils.Utils;

import java.sql.*;
import java.util.*;

public class AllDishesDao extends PostgreDaoAbstract {
    @Override
    public String get() {
        String query = "select  c.dish_id id, d.tittle from marathon.dishes d " +
                "join marathon.cooking c on c.dish_id = d.id order by d.tittle;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            ArrayList<GraficEntity> result = new ArrayList<>();
            while (rs.next()) {
                String recipeID = rs.getString(1);
                String tittle = rs.getString(2);
                int c = 0;
                String key = "";
                while (c < (tittle.length() - 1)) {
                    key = String.valueOf(tittle.charAt(c)).toLowerCase();
                    if (key.matches("[A-Za-zА-Яа-я]")) {
                        break;
                    }
                    c++;
                }
                String finalKey = key;
                GraficEntity graficEntity = result.stream()
                        .filter((p) -> (p.getName().equalsIgnoreCase(finalKey)))
                        .findFirst().orElse(null);
                if (Objects.isNull(graficEntity)) {
                    graficEntity = new GraficEntity(key, null);
                    result.add(graficEntity);
                }
                graficEntity.addFood(new FoodEntity(recipeID, tittle));
            }
            result.sort(Comparator.comparing(GraficEntity::getName));
            return Utils.objectToJson(result);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public String put(String string) {
        return null;
    }
}
