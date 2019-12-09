package testjava;

import static spark.Spark.*;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import spark.Request;
import spark.Response;
import spark.Route;

public class JavaServer {
    public static void main(String[] args) {
    	String databaseUrl = "jdbc:mysql://localhost/spark";
    	 
    	ConnectionSource connectionSource;
		try {
			connectionSource = new JdbcConnectionSource(databaseUrl);
		    ((JdbcConnectionSource)connectionSource).setUsername("spark");
		    ((JdbcConnectionSource)connectionSource).setPassword("spark");
		    
			TableUtils.createTableIfNotExists(connectionSource, User.class);
			Dao<User, String> userDao = DaoManager.createDao(connectionSource, User.class);
			
			postCreateSQL(connectionSource, userDao);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void postCreateSQL(ConnectionSource connectionSource, Dao<User, String> userDao) {
	   
        get("/hello", (req, res) -> "Hello World");
        get("/users/:id", new Route() {
        	@Override
        	public Object handle(Request req, Response res) {
    			return "test";
    		}
        });
        post("/users", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String username = request.queryParams("username");
                String email = request.queryParams("email");
                
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
         
                try{
                	userDao.create(user);
                } catch (SQLException e) {
                	e.printStackTrace();
                }
         
                response.status(201); // 201 Created
                return "done! 201";
             }
        });
    }
}
