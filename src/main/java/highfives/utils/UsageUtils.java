package highfives.utils;

import java.util.Date;
import java.sql.Connection; 
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import highfives.database.DatabaseConnection;
import highfives.data.UserUsage;
import highfives.Constants;


public final class UsageUtils {

    // Creates a new entry for client
    public static void createUsageEntry(String clientid) {
        
        String createEntryQuery = ((new StringBuilder())
                        .append("INSERT INTO api_usage(clientid, units) VALUES('")
                        .append(clientid)
                        .append("',")
                        .append(Constants.MONTHLY_UNITS)
                        .append(")")).toString();
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(createEntryQuery);
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Check whether client exists in the database
    public static boolean checkClientExists(String clientid) {
        
        String unitsQuery = ((new StringBuilder())
                        .append("SELECT id FROM api_usage WHERE clientid='")
                        .append(clientid)
                        .append("'")).toString();
             
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(unitsQuery);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (Exception e) {
            return false;
        }
    }

    // Gets the usage information for a client
    public static UserUsage getUserUsage(String clientid) {
        
        UserUsage usage = null;

        String unitsQuery = ((new StringBuilder())
                        .append("SELECT units, last_called, last_reset FROM api_usage WHERE clientid='")
                        .append(clientid)
                        .append("'")).toString();
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(unitsQuery);
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            int units = (int) resultSet.getLong(1);

            Timestamp last_called_timestamp  = (Timestamp) resultSet.getTimestamp(2);
            Date last_called = new Date(last_called_timestamp.getTime());

            Timestamp last_reset_timestamp  = (Timestamp) resultSet.getTimestamp(3);
            Date last_reset = new Date(last_reset_timestamp.getTime());

            usage = new UserUsage(clientid, units, last_called, last_reset);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return usage;
    }

    // Subtracts used units for a client
    public static void updateUsageUnits(String clientid, int usedUnits) {
        
        String updateUnitsQuery = ((new StringBuilder())
                        .append("UPDATE api_usage SET units=units-")
                        .append(usedUnits)
                        .append(", last_called=CURRENT_TIMESTAMP() WHERE clientid='")
                        .append(clientid)
                        .append("'")).toString();
        
        System.out.println(updateUnitsQuery);

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(updateUnitsQuery);
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }   

    // Resets number of usage units for a client, to the monthly quota
    public static void resetUsageUnits(String clientid) {
        
        String updateUnitsQuery = ((new StringBuilder())
                        .append("UPDATE api_usage SET units=")
                        .append(Constants.MONTHLY_UNITS)
                        .append(" and last_reset=CURRENT_TIMESTAMP() WHERE clientid='")
                        .append(clientid)
                        .append("'")).toString();
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(updateUnitsQuery);
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}
