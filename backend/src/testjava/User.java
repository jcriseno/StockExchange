package testjava;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
 
@DatabaseTable(tableName = "users")
public class User {
 
       @DatabaseField(generatedId = true)
    private int user_id;
 
       @DatabaseField
    private String username;

       @DatabaseField
       private double funds;
 
       public User() {
        // ORMLite needs a no-arg constructor
     }
 
       public int getId() {
        return this.user_id;
    }
 
       public String getUsername() {
            return this.username;
    }
 
       public void setUsername(String username) { this.username = username; }


    public double getFunds() {return this.funds;}

    public void setFunds(double funds) {this.funds = funds;}
}