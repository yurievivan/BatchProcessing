package jdbc.batch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbConnection {

    /**
     *
     * @author ivan.yuriev
     */
    private static final Logger LOG = LogManager.getLogger(DbConnection.class);
    private static final String CONNECTION_PROPERTY_FILE = "connection.properties";
    private static Connection con = null;
    private static String url;
    private static Properties properties;

    private enum ConnectionData {
        URL("url"),
        USER("user"),
        PASSWORD("password");
        String name;

        ConnectionData(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
    }
    
    private DbConnection(){
        
    }

    public static Connection getConnection() {
        try {
            if (con == null) {
                properties = getProperty();
                url = properties.get(ConnectionData.URL.getName()).toString();
                con = DriverManager.getConnection(url, properties);
            } else if (con.isClosed()) {
                con = DriverManager.getConnection(url, properties);
            }
        } catch (SQLException e) {
            LOG.error(e);
        }
        return con;
    }

    private static Properties getProperty() {
        Properties prop = new Properties();
        try (InputStream inputStream = DbConnection.class.getClassLoader().getResourceAsStream(CONNECTION_PROPERTY_FILE)) {
            prop.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()));
        } catch (Exception ex) {
            LOG.error(ex);
        }
        return prop;
    }

}
