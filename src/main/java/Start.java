import com.auth0.jwt.JWT;
import dao.Dao;
import dao.DishesDao;
import dao.MarathonListDao;
import dao.UsersDao;

public class Start {
    public static void main(String ... args){

        Dao dao = new DishesDao();
        dao.get();

    }
}
