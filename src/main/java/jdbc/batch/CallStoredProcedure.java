package jdbc.batch;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ivan.yuriev
 */
public class CallStoredProcedure extends BatchExecutor {

    private static final Logger LOG = LogManager.getLogger(CallStoredProcedure.class);
    private final static int BATCH_SIZE = 50;
    private final static String TABLE_NAME = "book";
    private final static String PROCEDURE_NAME = "insert_book";
    private final static String COMMAND = "call insert_book(?)";
    private final static String DROP_TABLE = "DROP table if exists book";
    private final static String CREATE_TABLE = "create table book ( "
                                                        + " id  bigserial not null, "
                                                        + " title varchar(50), "
                                                        + " primary key (id) "
                                                        + ")";
    private final static String CREATE_PROCEDURE = "CREATE PROCEDURE insert_book(bookTitle varchar(50)) "
                                                        + "AS $$ "
                                                        + "BEGIN "
                                                        + "   INSERT INTO book(title) "
                                                        + "    SELECT bookTitle "
                                                        + "    WHERE NOT EXISTS ( "
                                                        + "        SELECT 1 FROM book WHERE title=bookTitle "
                                                        + "    ); "
                                                        + "END ; "
                                                        + "$$ "
                                                        + "LANGUAGE plpgsql;";
    
    public CallStoredProcedure() {
        super(LOG);
        prepareAll();
    }
    
    private void prepareAll(){
        Set<String> procedureNames = getProcedureNames();
        Set<String> tableNames = getTableNames();
        if (!procedureNames.contains(PROCEDURE_NAME)) createProcedure();
        if (!tableNames.contains(TABLE_NAME)) createTable();
    }

    public void runStoredProcedure(List<Book> books) {
        if (books == null || books.isEmpty()) return;
        try (Connection connection = DbConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (CallableStatement cstmt = connection.prepareCall(COMMAND)) {
                for (int i = 0; i < books.size(); i++) {                    
                    getBatchUpdateLog().add(String.join(" ", "Query:", COMMAND, "title:", books.get(i).getTitle()));                   
                    cstmt.setString(1, books.get(i).getTitle()); 
                    cstmt.addBatch();

                    if (((i + 1) % BATCH_SIZE == 0) || (i == (books.size() - 1))) {
                        executeBatch(connection, cstmt);
                    }
                }
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        } finally {
            getBatchUpdateLog().clear();
        }
    }

    private Set<String> getProcedureNames() {
        Set<String> result = new HashSet<>();
        try (Connection connection = DbConnection.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet res = meta.getProcedures(null, null, null)) {
                while (res.next()) {
                    result.add(res.getString("PROCEDURE_NAME"));
                }
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return result;
    }
    
    private Set<String> getTableNames() {
        Set<String> result = new HashSet<>();
        try (Connection connection = DbConnection.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet res = meta.getTables(null, null, "%", null)) {
                while (res.next()) {
                    result.add(res.getString("TABLE_NAME"));
                }
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return result;
    }

    public void dropTable() {
        executeQuery(DROP_TABLE);
    }

    public void createTable() {
        executeQuery(CREATE_TABLE);
    }
    
    public void createProcedure() {
        executeQuery(CREATE_PROCEDURE);
    }

    private void executeQuery(String query) {
        try (Connection connection = DbConnection.getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeQuery(query);
        } catch (SQLException ex) {
            LOG.error(ex);
        }
    }

}
