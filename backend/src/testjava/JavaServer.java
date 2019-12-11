import static spark.Spark.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class JavaServer {
    private static String responseTest = "test passed!";

    public static void main(String[] args) {
        port(8000);
        get("/test", (request, response) -> {
            return responseTest;
        });

        String databaseUrl = "jdbc:mysql://ec2-184-72-87-247.compute-1.amazonaws.com/stockexchange";

        ConnectionSource connectionSource = null;
        Dao<User, String> userDao;
        Dao<Transactions, String> txnDao;
        Dao<Stock, String> stockDao;
        Dao<Company, String> comDao;
        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);
            ((JdbcConnectionSource) connectionSource).setUsername("root");
            ((JdbcConnectionSource) connectionSource).setPassword("lart2456");

            TableUtils.createTableIfNotExists(connectionSource, User.class);
            userDao = DaoManager.createDao(connectionSource, User.class);
            txnDao = DaoManager.createDao(connectionSource, Transactions.class);
            stockDao = DaoManager.createDao(connectionSource, Stock.class);
            comDao = DaoManager.createDao(connectionSource, Company.class);

            postGetUser(connectionSource, userDao);
            postGetTxn(connectionSource, txnDao);
            postGetCom(connectionSource, comDao);
            postGetStock(connectionSource, stockDao);
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            responseTest = sw.toString();
        }
    }

    private static void postGetUser(ConnectionSource connectionSource, Dao<User, String> userDao) throws SQLException {
        post("/users", (request, response) -> {
            String username = request.queryParams("username");
            String deposit = request.queryParams("funds");
            Double funds = Double.valueOf(deposit);

            User user = new User();
            user.setUsername(username);
            user.setFunds(funds);

            userDao.create(user);

            response.status(201); // 201 Created
            return "done! 201";
        });

        get("/retrieveUserID/:user", (request, response) -> {
            String hold = ":user";
            GenericRawResults<String[]> results;
            results = userDao.queryRaw("SELECT user_id FROM users WHERE username = " + hold);

            List<String[]> resArray = results.getResults();
            String[] fin = resArray.get(0);


            if (fin[0] != null) {
                response.status(201);
                return fin[0];
            } else {
                response.status(404); // 404 Not found
                return "404: User not found";
            }
        });
    }

    private static void postGetTxn(ConnectionSource connectionSource, Dao<Transactions, String> txnDao) throws SQLException {
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

    private static void postGetCom(ConnectionSource connectionSource, Dao<Company, String> comDao) throws SQLException {

    }

    private static void postGetStock(ConnectionSource connectionSource, Dao<Stock, String> stockDao) throws SQLException {
        post("/purchase", (request, response) -> {
            String userID = request.queryParams("user");
            String stockID = request.queryParams("stock_id");
            String ticker = request.queryParams("company");
            String quantity = request.queryParams("quantity");

            Stock stock = new Stock();
            stock.setUser(userID);
            stock.setStock_id(Integer.parseInt(stockID));
            stock.setCompany(ticker);
            stock.setQuantity(Integer.parseInt(quantity));

            stockDao.create(stock);

            response.status(201); // 201 Created
            return "done! 201";
        });
    }
}
