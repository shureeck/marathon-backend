package dao;

import entities.GraficEntity;
import entities.RecipeEntity;
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

                String key = String.valueOf(tittle.charAt(0)).toLowerCase();
                GraficEntity graficEntity = result.stream()
                        .filter((p) -> (p.getName().equalsIgnoreCase(key)))
                        .findFirst().orElse(null);
                if (Objects.isNull(graficEntity)) {
                    graficEntity = new GraficEntity(key, null);
                    result.add(graficEntity);
                }
                graficEntity.addFood(Map.of(tittle, recipeID));
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
