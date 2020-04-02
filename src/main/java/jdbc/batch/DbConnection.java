package jdbc.batch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public class DbConnection {

    /**
     *
     * @author ivan.yuriev
     */
    
    private static final Logger LOG = Logger.getLogger(DbConnection.class);
    private static final String CONNECTION_PROPERTY_FILE = "connection.properties";
    private static Connection con = null;
    private static String url;
    private static String driver;
    private static String user;
    private static String password;

    private enum ConnectionData {
        DRIVER,
        URL,
        USER,
        PASSWORD
    }

    public static Connection getConnection() {
        try {
            if (con == null) {
                Map<String, String> property = getProperty();
                driver = property.get(ConnectionData.DRIVER.name());
                url = property.get(ConnectionData.URL.name());
                user = property.get(ConnectionData.USER.name());
                password = property.get(ConnectionData.PASSWORD.name());
                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url, user, password);
            } else if (con.isClosed()) {
                con = DriverManager.getConnection(url, user, password);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            LOG.error(e);
        }
        return con;
    }

    private static Map<String, String> getProperty() {
        Map<String, String> prop = new HashMap<>();
        try (InputStream inputStream = DbConnection.class.getClassLoader().getResourceAsStream(CONNECTION_PROPERTY_FILE)) {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()));
            properties.stringPropertyNames().stream().forEach((key) -> {
                prop.put(key, properties.get(key).toString());
            });
        } catch (Exception ex) {
            LOG.error(ex);
        }
        return prop;
    }

}
