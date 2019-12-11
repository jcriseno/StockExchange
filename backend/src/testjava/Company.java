import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "company")
public class Company {

    public Company() {

    }

    @DatabaseField(id = true, columnName = "company_ticker")
    private String ticker;

    @DatabaseField(columnName = "company_name")
    private String name;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

