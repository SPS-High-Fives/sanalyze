package highfives.database;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException; 
import highfives.Constants;


public class DatabaseConnection { 
  
    private static Connection connection = null; 
  
    static
    { 
        try { 
            Class.forName("com.mysql.jdbc.Driver"); 
            connection = DriverManager.getConnection(Constants.JDBC_URL); 
        } 
        catch (ClassNotFoundException | SQLException e) { 
            e.printStackTrace(); 
        } 
    } 
    
    public static Connection getConnection() 
    { 
        return connection; 
    } 
}
