import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "transactions")
public class Transactions {

    public Transactions(){

    }

    @DatabaseField(generatedId = true)
    private int transaction_id;

    @DatabaseField(columnName = "user_id")
    private int user_id;

    @DatabaseField(columnName = "company_ticker")
    private String ticker;

    @DatabaseField
    private Timestamp timestamp;

    @DatabaseField
    private int quantity;

    @DatabaseField
    private double buying_price;

    // GETTERS AND SETTERS

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getCompany_id() {
        return ticker;
    }

    public void setCompany_id(String ticker) {
        this.ticker = ticker;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getBuying_price() {
        return buying_price;
    }

    public void setBuying_price(double buying_price) {
        this.buying_price = buying_price;
    }





}