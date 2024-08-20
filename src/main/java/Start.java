import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dao.*;
import enums.Days;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class Start {
    public static void main(String... args) {
        Dao d = UsersDao.builder().marathonId("1").build();
        d.get();
       /* JWTVerifier verifier = JWT.require(Algorithm.HMAC256("key".getBytes())).build();
        try {
            DecodedJWT decodedJWT = verifier.verify("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdG5hbWUiOiJPbGVrc2FuZHIiLCJyb2xlIjoiQWRtaW4iLCJpZCI6IjEiLCJ1c2VybmFtZSI6InBvbGlha292YWxlZWtAZ21haWwuY29tIiwibGFzdG5hbWUiOiJQb2xpYWtvdiIsImV4cGlyYXRpb24iOiIyMDI0LTAyLTEyVDEwOjI5OjI5LjA1NloifQ.QjNePJDtkiJe_6QxWH09U1pbOIUpd3zMkT-pH7h91HI");
        } catch (SignatureVerificationException e) {
        }

        Days.getDay(1);
        Dao dao = new  CookingDao(376);;
        String userData = dao.get();
     /*   Map<String, String> a =  Map.of("user", "Oleksandr", "role", "Admin");
        String token = JWT.create()
                .withPayload(Utils.objectToJson(a))
                .sign(Algorithm.HMAC256("keydddd".getBytes()));
        JWTVerifier verifier =  JWT.require(Algorithm.HMAC256("keydddd".getBytes())).build();
        verifier.verify(token);*/
        int i = 0;

      /*  Dao dao = new DishesDao();
        dao.get();*/

    }
}
