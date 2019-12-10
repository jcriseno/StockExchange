import static spark.Spark.*;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class JavaServer {
    public static void main(String[] args) throws SQLException {
        port(80);

        String databaseUrl = "jdbc:stockexchange://ec2-184-72-87-247.compute-1.amazonaws.com";

        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
        ((JdbcConnectionSource) connectionSource).setUsername("myuser");
        ((JdbcConnectionSource) connectionSource).setPassword("mypassword");

        TableUtils.createTableIfNotExists(connectionSource, User.class);
        Dao<User, String> userDao = DaoManager.createDao(connectionSource, User.class);
        Dao<Transactions, String> txnDao = DaoManager.createDao(connectionSource, Transactions.class);

        postCreateSQL(connectionSource, userDao);
        postQueryTxn(connectionSource, txnDao);
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

        get("/retrieveUser/:id", (request, response) -> {
            User user = null;
            try {
                user = userDao.queryForId(request.params(":id"));
            } catch (SQLException e) {
            }
            if (user != null) {
                return "User: " + user;
            } else {
                response.status(404); // 404 Not found
                return "404: User not found";
            }
        });
    }

    private static void postQueryTxn(ConnectionSource connectionSource, Dao<Transactions, String> txnDao) throws SQLException {
        get("/getTransaction/:id", (request, response) -> {
            Transactions txn = null;
            try {
                txn = txnDao.queryForId(request.params(":id"));
            } catch (SQLException e) {
            }
            if (txn != null) {
                return "Transaction: " + txn;
            } else {
                response.status(404); // 404 Not found
                return "404: User not found";
            }
        });
    }
}
