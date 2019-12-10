import static spark.Spark.*;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class JavaServer {
    public static void main(String[] args) throws SQLException {
    	String databaseUrl = "jdbc:mysql://localhost/spark";
    	 
    	ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
    	((JdbcConnectionSource)connectionSource).setUsername("spark");
    	((JdbcConnectionSource)connectionSource).setPassword("spark");
		    
    	TableUtils.createTableIfNotExists(connectionSource, User.class);
    	Dao<User, String> userDao = DaoManager.createDao(connectionSource, User.class);

    	postCreateSQL(connectionSource, userDao);
    }
    
    private static void postCreateSQL(ConnectionSource connectionSource, Dao<User, String> userDao) throws SQLException {
        get("/test", (request, response) -> {
            return "test passed!";
        });
        post("/users", (request, response) -> {
            String username = request.queryParams("username");

            User user = new User();
            user.setUsername(username);

            userDao.create(user);

            response.status(201); // 201 Created
            return "done! 201";
         });
    }
}
