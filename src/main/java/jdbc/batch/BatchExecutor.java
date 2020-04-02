package jdbc.batch;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author ivan.yuriev
 */
public class BatchExecutor {

    private final Logger log;
    private final BatchUpdateLog batchUpdateLog;

    public BatchExecutor(Logger log) {
        this.log = log;
        batchUpdateLog = new BatchUpdateLog(log);
    }

    void executeBatch(Connection connection, Statement stmt) throws SQLException {
        try {
            int[] result = stmt.executeBatch();
            log.info("Number of rows affected: " + result.length);
            connection.commit();
            batchUpdateLog.clear();
        } catch (BatchUpdateException ex) {
            batchUpdateLog.log(ex);
            batchUpdateLog.clear();
            connection.rollback();
        }
    }

    BatchUpdateLog getBatchUpdateLog() {
        return batchUpdateLog;
    }

}
