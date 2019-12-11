import static spark.Spark.*;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import spark.Filter;

public class JavaServer {
    private static String responseTest = "test passed!";

    public static void main(String[] args) {
        initExceptionHandler((e) -> {
            e.printStackTrace();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            responseTest = sw.toString();
        });

        port(8000);
        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST");
        });
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
            ObjectMapper userMap = new ObjectMapper();
            return userMap.writeValueAsString(user);
        });

        get("/retrieveUser/:user", (request, response) -> {
            String userName = request.params(":user");
            QueryBuilder<User, String> qbUser = userDao.queryBuilder();
            qbUser.where().eq("username", userName);
            User results = userDao.queryForFirst(qbUser.prepare());

            responseTest += "<br>Requested User ID for Username " + userName;

            if (results != null) {
                response.status(201);
                ObjectMapper userMap = new ObjectMapper();
                return userMap.writeValueAsString(results);
            } else {
                response.status(404); // 404 Not found
                return "Error: User " + userName + " not found";
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
                response.status(202);
                return "Transaction: " + txn;
            } else {
                response.status(404); // 404 Not found
                return "404: User not found";
            }
        });
    }

    private static void postGetCom(ConnectionSource connectionSource, Dao<Company, String> comDao) throws SQLException {
        get("/retrieveCompany/:ticker", (request, response) -> {
            String ticker = request.params(":ticker");
            QueryBuilder<Company, String> qbCompany = comDao.queryBuilder();
            qbCompany.where().eq("ticker", ticker);
            Company results = comDao.queryForFirst(qbCompany.prepare());

            responseTest += "<br>Requested Company for Company Ticker " + ticker;

            if (results != null) {
                response.status(201);
                ObjectMapper companyMap = new ObjectMapper();
                return companyMap.writeValueAsString(results);
            } else {
                response.status(404); // 404 Not found
                return "Error: Company " + ticker + " not found";
            }
        });
    }

    private static void postGetStock(ConnectionSource connectionSource, Dao<Stock, String> stockDao) throws SQLException {
        post("/purchase", (request, response) -> {
            String userID = request.queryParams("user_id");
            String stockID = request.queryParams("stock_id");
            String ticker = request.queryParams("ticker");
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

        get("/getStock", (request, response) -> {
            String userID = request.queryParams("user_id");
            String ticker = request.queryParams("ticker");

            QueryBuilder<Stock, String> qbStock = stockDao.queryBuilder();
            qbStock.where().eq("user_id", userID).and()
            .eq("ticker", ticker);
            Stock results = stockDao.queryForFirst(qbStock.prepare());

            return results;
        });

        get("/retrieveStock/:stock", (request, response) -> {
            String stockID = request.params(":stock");
            responseTest += "<br>GOt params " + stockID;
            QueryBuilder<Stock, String> qbStock = stockDao.queryBuilder();
            qbStock.where().eq("stock_id", stockID);
            responseTest += "<br>set up query " + stockID;
            Stock results = stockDao.queryForFirst(qbStock.prepare());

            responseTest += "<br>Requested Stock for Stock ID " + stockID;

            if (results != null) {
                response.status(201);
                ObjectMapper stockMap = new ObjectMapper();
                return stockMap.writeValueAsString(results);
            } else {
                response.status(404); // 404 Not found
                return "Error: Stock " + stockID + " not found";
            }
        });
        get("/retrieveStocksByUser/:userid", (request, response) -> {
            String userID = request.params(":userid");
            QueryBuilder<Stock, String> qbUser = stockDao.queryBuilder();
            qbUser.where().eq("user_id", userID);
            List<Stock> results = stockDao.query(qbUser.prepare());

            responseTest += "<br>Requested all stocks for User ID " + userID;

            if (results != null) {
                response.status(201);
                ObjectMapper stocksMap = new ObjectMapper();
                return stocksMap.writeValueAsString(results);
            } else {
                response.status(404); // 404 Not found
                return "Error: User " + userID + " not found";
            }
        });
    }
}
