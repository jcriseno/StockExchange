package testjava;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "transactions")
public class Transactions {

    public Transactions(){

    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private int transaction_id;

    @DatabaseField(foreign = true)
    private int company_id;

    @DatabaseField
    private Timestamp timestamp;

    @DatabaseField
    private int quantity;

    @Database
    private double buying_price;

    // GETTERS AND SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
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