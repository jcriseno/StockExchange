import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "transactions")
public class Transactions {

    public Transactions(){

    }

    @DatabaseField(generatedId = true)
    private int transaction_id;

    @DatabaseField(foreign = true, columnName = "user_id")
    private User user;

    @DatabaseField(foreign = true, columnName = "ticker")
    private Company company;

    @DatabaseField
    private Timestamp timestamp;

    @DatabaseField
    private int quantity;

    @DatabaseField
    private double buying_price;

    // GETTERS AND SETTERS

    public User getUser() {
        return user;
    }

    public void setUser() {
        this.user = user;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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