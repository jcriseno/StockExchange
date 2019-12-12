import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "stock")
public class Stock {

    public Stock(){

    }

    @DatabaseField(generatedId = true)
    private int stock_id;

    @DatabaseField(columnName = "user_id")
    private int user_id;

    @DatabaseField(columnName = "company_ticker")
    private String company;

    @DatabaseField
    private int quantity;


    public int getStock_id() {
        return this.stock_id;
    }

    public void setStock_id(int id) {
        this.stock_id = id;
    }

    public int getUser() {
        return this.user_id;
    }

    public void setUser(int user_id) {
        this.user_id = user_id;
    }

    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company){
        this.company = company;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}