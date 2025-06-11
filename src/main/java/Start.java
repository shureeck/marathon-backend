import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import dao.*;
import entities.AIResponse;
import enums.Days;
import enums.Languages;
import utils.Utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static utils.Utils.jsonToObject;
import static utils.Utils.translate;

public class Start {
    public static void main(String... args) throws IOException, GeneralSecurityException {


        Dao dao = new CookingDao(376);
        String data = dao.get();
       String translated= translate(data, Languages.polish);
       int i=0;
        // Dao d = UsersDao.builder().marathonId("1").build();
        //   d.get();

        Translate t = new Translate.Builder(
                GoogleNetHttpTransport.newTrustedTransport()
                , GsonFactory.getDefaultInstance(), null)
                // Set your application name
                .setApplicationName("Stackoverflow-Example")
                .build();
        Translate.Translations.List list = t.new Translations().list(
                Arrays.asList(
                        // Pass in list of strings to be translated
                        "Hello World",
                        "How to use Google Translate from Java"),
                // Target language
                "ES");

        // TODO: Set your API-Key from https://console.developers.google.com/
        list.setKey("AIzaSyANu_hn7rdxWTW_IamL2zBym22halr6OPk");
        TranslationsListResponse response = list.execute();
        for (TranslationsResource translationsResource : response.getTranslations()) {
            System.out.println(translationsResource.getTranslatedText());
        }
    }

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
