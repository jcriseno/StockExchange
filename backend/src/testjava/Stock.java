import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "stocks")
public class Stock {

    public Stock(){

    }

    @DatabaseField(generatedId = true)
    private int stock_id;

    @DatabaseField(foreign = true, columnName = "user_id")
    private User user;

    @DatabaseField(foreign = true, columnName = "ticker")
    private Company company;

    @DatabaseField
    private int quantity;


    public int getStock_id() {
        return stock_id;
    }

    public void setStock_id(int id) {
        this.stock_id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company){
        this.company = company;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}