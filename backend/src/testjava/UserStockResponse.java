import java.util.List;

public class UserStockResponse {
    private User user;
    private List<Stock> stocks;

    public User getUser(){
        return this.user;
    }

    public List<Stock> getStocks() {
        return this.stocks;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStocks(List<Stock> stocks){
        this.stocks = stocks;
    }
}
