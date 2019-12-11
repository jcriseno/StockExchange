import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "stocks")
public class Stock {

    public Stock(){

    }

    @DatabaseField(generatedId = true)
    private int stock_id;

    @DatabaseField(columnName = "user_id")
    private String user;

    @DatabaseField(columnName = "ticker")
    private String company;

    @DatabaseField
    private int quantity;


    public int getStock_id() {
        return stock_id;
    }

    public void setStock_id(int id) {
        this.stock_id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company){
        this.company = company;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}