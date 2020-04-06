package jdbc.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ivan.yuriev
 */
public class BatchExecutor {

    private final Logger log;
    private final BatchUpdateLog batchUpdateLog;
    private static final String NUMBER_ROWS = "Number of rows affected: %d";

    public BatchExecutor(Logger log) {
        this.log = log;
        batchUpdateLog = new BatchUpdateLog(log);
    }

    void executeBatch(Connection connection, Statement stmt) {
        try {
            int[] result = stmt.executeBatch();
            String info = String.format(NUMBER_ROWS, result == null ? 0 : result.length);
            log.info(info);
            connection.commit();
            batchUpdateLog.clear();
        } catch (SQLException ex) {
            log(connection, ex);
        }
    }

    BatchUpdateLog getBatchUpdateLog() {
        return batchUpdateLog;
    }

    private void log(Connection connection, SQLException ex) {
        try {
            batchUpdateLog.log(ex);
            connection.rollback();
        } catch (SQLException sqlEx) {
            log.error(sqlEx);
        }
    }

}
