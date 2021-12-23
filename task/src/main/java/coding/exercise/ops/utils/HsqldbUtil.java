package coding.exercise.ops.utils;

import coding.exercise.ops.constants.AppConstants;
import coding.exercise.ops.model.Event;
import org.apache.logging.log4j.*;
import java.sql.*;

public class HsqldbUtil {

    private static final Logger logger = LogManager.getLogger(HsqldbUtil.class);

    private final String dbUrl = "jdbc:hsqldb:hsql://localhost/testdb";
    private final String jdbcDriver = "org.hsqldb.jdbc.JDBCDriver";

    private Connection con = null;
    private Statement stmt = null;
    private int result = 0;

    /* Drops the table */
    private void dropTable() throws SQLException {
        try {
            con = getConnection();
            stmt = con.createStatement();
            result = stmt.executeUpdate("DROP TABLE '"+ AppConstants.EVENTSTABLE +"'");
            logger.info("Table dropped successfully");
        }
        catch (Exception e) {
            logger.debug("{}",e.toString());
        }
        finally {
            stmt.close();
        }
    }

    /* creates the events table if it doesnt exist already */
    public void createTable() throws SQLException {
        try {
            dropTable();
            con = getConnection();
            stmt = con.createStatement();
            ResultSet tables = isTableAvailable();
            if (!tables.next()) {
                logger.info("Table exists");
            }
            else{
                result = stmt.executeUpdate("CREATE TABLE events (id VARCHAR(20) NOT NULL, duration INT NOT NULL,type VARCHAR(20), host VARCHAR(20), alert BOOLEAN, PRIMARY KEY (id)); ");
                con.commit();
                logger.info("Table created");
            }
        }  catch (Exception e) {
            logger.debug("{}",e.toString());
        }
        finally{
            stmt.close();
        }
    }

    /* creates an event row in the table from the Event object */
    public void insertRow(Event event) throws SQLException {
        try {
            con = getConnection();
            ResultSet tables = isTableAvailable();
            if (!tables.next()) {
                stmt = con.createStatement();
                result = stmt.executeUpdate("INSERT INTO events VALUES ('"+ event.getId() + "' ," + event.getDuration() + ", '" + event.getType() + "', '" + event.getHost() + "', " + event.getAlert()+ ")");
                logger.info("{} rows affected",result);
                logger.info("Rows inserted successfully");
                con.commit();
            }
            else{
                logger.info("Doesn't exist");
            }

        }catch (Exception e) {
            logger.debug("{}",e.toString());
        }
    }
    /* Prints a snapshot of the table if it exists */
    public void selectAllStatement() throws SQLException {
        ResultSet result = null;
        try {
            con = getConnection();
            ResultSet tables = isTableAvailable();
            if (!tables.next()) {
                result = stmt.executeQuery("SELECT * FROM "+ AppConstants.EVENTSTABLE);
                while (result.next()) {
                    System.out.println(result.getString("id") + " | " +
                            result.getInt("duration") + " | " +
                            result.getString("type") + " | " +
                            result.getString("host") + " | " +
                            result.getBoolean("alert"));
                }
            } else {
                logger.info("table doesn't exist");
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* Method used to get the connection. Sychronized so its thread safe */
    private synchronized Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(jdbcDriver);
        return DriverManager.getConnection(dbUrl, "SA", "");
    }

    /* Check to see if the table is already created or not */
    private ResultSet isTableAvailable() throws SQLException {
        DatabaseMetaData dbm = con.getMetaData();
        return dbm.getTables(null, null, AppConstants.EVENTSTABLE, null);
    }

}
