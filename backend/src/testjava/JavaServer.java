import static spark.Spark.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class JavaServer {
    private static String responseTest = "test passed!";

    public static void main(String[] args) {
        port(80);
        get("/test", (request, response) -> {
            return responseTest;
        });

        String databaseUrl = "jdbc:mysql://ec2-184-72-87-247.compute-1.amazonaws.com/stockexchange";

        ConnectionSource connectionSource = null;
        Dao<User, String> userDao;
        Dao<Transactions, String> txnDao;
        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);
            ((JdbcConnectionSource) connectionSource).setUsername("root");
            ((JdbcConnectionSource) connectionSource).setPassword("lart2456");

            TableUtils.createTableIfNotExists(connectionSource, User.class);
            userDao = DaoManager.createDao(connectionSource, User.class);
            txnDao = DaoManager.createDao(connectionSource, Transactions.class);

            postCreateSQL(connectionSource, userDao);
            postQueryTxn(connectionSource, txnDao);
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            responseTest = sw.toString();
        }
    }

    private static void postCreateSQL(ConnectionSource connectionSource, Dao<User, String> userDao) throws SQLException {
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
