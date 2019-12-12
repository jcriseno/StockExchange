import static spark.Spark.*;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;

public class JavaServer {
    private static String responseTest = "test passed!";
    private static Dao<User, String> userDao;
    private static Dao<Transactions, String> txnDao;
    private static Dao<Stock, String> stockDao;
    private static Dao<Company, String> comDao;
    private static JdbcConnectionSource connectionSource;

    public static void main(String[] args) {
        initExceptionHandler((e) -> {
            e.printStackTrace();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            responseTest = sw.toString();
        });

        exception(Exception.class, (e, request, response) -> {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            responseTest = sw.toString();
        });

        port(8000);
        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST");
        });
        get("/test", (request, response) -> {
            return responseTest;
        });

        String databaseUrl = "jdbc:mysql://ec2-54-175-158-115.compute-1.amazonaws.com/stockexchange";

        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);
            connectionSource.setUsername("root");
            connectionSource.setPassword("lart2456");

            TableUtils.createTableIfNotExists(connectionSource, User.class);
            userDao = DaoManager.createDao(connectionSource, User.class);
            txnDao = DaoManager.createDao(connectionSource, Transactions.class);
            stockDao = DaoManager.createDao(connectionSource, Stock.class);
            comDao = DaoManager.createDao(connectionSource, Company.class);

            postGetUser();
            postGetTxn();
            postGetCom();
            postGetStock();
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            responseTest = sw.toString();
        }
    }

    private static void postGetUser() throws SQLException {
        post("/users", (request, response) -> {
            String userName = request.queryParams("username");
            String deposit = request.queryParams("funds");
            double funds = Double.parseDouble(deposit);

            QueryBuilder<User, String> qbUser = userDao.queryBuilder();
            qbUser.where().eq("username", userName);

            User user = userDao.queryForFirst(qbUser.prepare());
            if(user != null) {
                user.setFunds(funds);

                updateUserColumn("funds", String.valueOf(user.getFunds()), user.getId());
            } else {
                user = new User();
                user.setUsername(userName);
                user.setFunds(funds);

                userDao.create(user);
            }

            response.status(201); // 201 Created
            ObjectMapper userMap = new ObjectMapper();
            return userMap.writeValueAsString(user);
        });

        get("/retrieveUser/:user", (request, response) -> {
            String userName = request.params(":user");
            QueryBuilder<User, String> qbUser = userDao.queryBuilder();
            qbUser.where().eq("username", userName);
            User user = userDao.queryForFirst(qbUser.prepare());

            responseTest += "<br>Requested login for Username " + userName;

            if (user != null) {
                response.status(201);

                List<Stock> resultStocks = getStocksByUser(user.getId());

                UserStockResponse usr = new UserStockResponse();
                usr.setUser(user);
                usr.setStocks(resultStocks);

                ObjectMapper userMap = new ObjectMapper();
                return userMap.writeValueAsString(usr);
            } else {
                response.status(404); // 404 Not found
                return "Error: User " + userName + " not found";
            }
        });
    }

    private static void postGetTxn() throws SQLException {
        get("/getTransaction/:user_id", (request, response) -> {
            String user_id = ":user_id";

            QueryBuilder<Transactions, String> qbTxn = txnDao.queryBuilder();
            List<Transactions> results = qbTxn.where().eq("user_id", user_id).query();


            if (results != null) {
                response.status(202);
                ObjectMapper txnMap = new ObjectMapper();
                return txnMap.writeValueAsString(results);
            } else {
                response.status(404); // 404 Not found
                return "404: User not found";
            }
        });

        get("/getCoTransaction/:company_ticker", (request, response) -> {
            String company_ticker = ":company_ticker";

            QueryBuilder<Transactions, String> qbTxn = txnDao.queryBuilder();
            List<Transactions> results = qbTxn.where().eq("company_ticker", company_ticker).query();

            if (results != null){
                response.status(202);
                ObjectMapper txnMap = new ObjectMapper();
                return txnMap.writeValueAsString(results);
            }
            else{
                response.status(404);
                return "404: Company_ticker not found";
            }
        });
    }

    private static void postGetCom() throws SQLException {
        get("/retrieveCompany/:ticker", (request, response) -> {
            String ticker = request.params(":ticker");
            QueryBuilder<Company, String> qbCompany = comDao.queryBuilder();
            qbCompany.where().eq("company_ticker", ticker);
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

    private static void postGetStock() throws SQLException {
        post("/purchase", (request, response) -> {
            String userID = request.queryParams("user_id");
            String ticker = request.queryParams("ticker");
            String quantity = request.queryParams("quantity");
            String pricePer = request.queryParams("price");
            double buying_price = Double.parseDouble(pricePer);

            QueryBuilder<Stock, String> qbStock = stockDao.queryBuilder();
            qbStock.where().eq("user_id", userID).and()
                    .eq("company_ticker", ticker).and()
                    .ge("quantity", quantity);
            Stock stock = stockDao.queryForFirst(qbStock.prepare());

            if(stock != null) {
                stock.setQuantity(stock.getQuantity() + Integer.parseInt(quantity));

                updateStockColumn("quantity", String.valueOf(stock.getQuantity()), stock.getStock_id());
            } else {
                stock = new Stock();
                stock.setUser(Integer.parseInt(userID));
                stock.setCompany(ticker);
                stock.setQuantity(Integer.parseInt(quantity));

                stockDao.create(stock);
            }

            logTransaction(stock.getQuantity(), buying_price, stock.getUser(), stock.getCompany());

            response.status(201); // 201 Created

            User user = getUserByID(Integer.parseInt(userID));
            user.setFunds(user.getFunds() - buying_price * Integer.parseInt(quantity));
            updateUserColumn("funds", String.valueOf(user.getFunds()), user.getId());

            List<Stock> resultStocks = getStocksByUser(user.getId());

            UserStockResponse usr = new UserStockResponse();
            usr.setUser(user);
            usr.setStocks(resultStocks);

            ObjectMapper stockMap = new ObjectMapper();
            return stockMap.writeValueAsString(usr);
        });

        post("/sell", (request, response) -> {
            String userID = request.queryParams("user_id");
            String ticker = request.queryParams("ticker");
            String quantity = request.queryParams("quantity");
            String pricePer = request.queryParams("price");
            double selling_price = Double.parseDouble(pricePer);

            QueryBuilder<Stock, String> qbStock = stockDao.queryBuilder();
            qbStock.where().eq("user_id", userID).and()
                    .eq("company_ticker", ticker).and()
                    .ge("quantity", quantity);
            Stock stock = stockDao.queryForFirst(qbStock.prepare());

            if(stock != null) {
                logTransaction(stock.getQuantity(), -1 * selling_price, stock.getUser(), stock.getCompany());

                stock.setQuantity(stock.getQuantity() - Integer.parseInt(quantity));
                if(stock.getQuantity() <= 0) {
                    stockDao.delete(stock);
                } else {
                    updateStockColumn("quantity", String.valueOf(stock.getQuantity()), stock.getStock_id());
                }

                User user = getUserByID(Integer.parseInt(userID));
                user.setFunds(user.getFunds() + selling_price * Integer.parseInt(quantity));

                updateUserColumn("funds", String.valueOf(user.getFunds()), user.getId());

                List<Stock> resultStocks = getStocksByUser(Integer.parseInt(userID));

                UserStockResponse sr = new UserStockResponse();
                sr.setUser(user);
                sr.setStocks(resultStocks);

                response.status(201); // 201 Created
                ObjectMapper userMap = new ObjectMapper();
                return userMap.writeValueAsString(sr);
            } else {
                response.status(404);
                return "Error: Stock with that quantity/company/user not found.";
            }
        });

        get("/retrieveStocksByTicker/:user_id/:ticker", (request, response) -> {
            String userID = request.queryParams("user_id");
            String ticker = request.queryParams("ticker");

            QueryBuilder<Stock, String> qbStock = stockDao.queryBuilder();
            qbStock.where().eq("user_id", userID).and()
            .eq("company_ticker", ticker);
            Stock results = stockDao.queryForFirst(qbStock.prepare());

            if (results != null) {
                response.status(201);
                ObjectMapper stocksMap = new ObjectMapper();
                return stocksMap.writeValueAsString(results);
            } else {
                response.status(404); // 404 Not found
                return "Error: Stock with " + userID + " and ticker " + ticker + " not found";
            }
        });

        get("/retrieveStock/:stock", (request, response) -> {
            String stockID = request.params(":stock");
            QueryBuilder<Stock, String> qbStock = stockDao.queryBuilder();
            qbStock.where().eq("stock_id", stockID);
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
            List<Stock> results = getStocksByUser(Integer.parseInt(userID));

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

    private static void logTransaction(int quantity, double buying_price, int user_id, String ticker) throws SQLException {
        LocalDateTime newDT = LocalDateTime.now();
        Timestamp newTime = Timestamp.valueOf(newDT);

        Transactions newTransaction = new Transactions();
        newTransaction.setTimestamp(newTime);
        newTransaction.setQuantity(quantity);
        newTransaction.setBuying_price(buying_price);
        newTransaction.setUser_id(user_id);
        newTransaction.setCompany_id(ticker);

        txnDao.create(newTransaction);
    }

    public static User getUserByID(int userID) throws SQLException {
        QueryBuilder<User, String> qbUser = userDao.queryBuilder();
        qbUser.where().eq("user_id", String.valueOf(userID));
        return userDao.queryForFirst(qbUser.prepare());
    }

    public static List<Stock> getStocksByUser(int userID) throws SQLException {
        QueryBuilder<Stock, String> qbStocks = stockDao.queryBuilder();
        qbStocks.where().eq("user_id", String.valueOf(userID));
        return stockDao.query(qbStocks.prepare());
    }

    public static void updateStockColumn(String columnName, String columnValue, int stockID) throws SQLException {
        UpdateBuilder<Stock, String> ubStock = stockDao.updateBuilder();
        ubStock.where().eq("stock_id", String.valueOf(stockID));
        ubStock.updateColumnValue(columnName, columnValue);
        ubStock.update();
    }

    public static void updateUserColumn(String columnName, String columnValue, int userID) throws SQLException {
        UpdateBuilder<User, String> ubUser = userDao.updateBuilder();
        ubUser.where().eq("user_id", String.valueOf(userID));
        ubUser.updateColumnValue(columnName, columnValue);
        ubUser.update();
    }
}
