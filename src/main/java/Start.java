import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import dao.*;
import enums.Days;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class Start {
    public static void main(String... args) {
        Days.getDay(1);
        Dao dao = new  CookingDao(376);;
        String userData = dao.get();
        Map<String, String> a =  Map.of("user", "Oleksandr", "role", "Admin");
        String token = JWT.create()
                .withPayload(Utils.objectToJson(a))
                .sign(Algorithm.HMAC256("keydddd".getBytes()));
        JWTVerifier verifier =  JWT.require(Algorithm.HMAC256("keydddd".getBytes())).build();
        verifier.verify(token);
        int i = 0;

      /*  Dao dao = new DishesDao();
        dao.get();*/

    }
}
