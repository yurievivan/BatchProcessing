package jdbc.batch;

import java.sql.BatchUpdateException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author ivan.yuriev
 */
public class BatchUpdateLog {
    private final List<String> sqlQueries = new CopyOnWriteArrayList<>();
    private final Logger log;

    public BatchUpdateLog(Logger log) {
        this.log = log;
    }

    public synchronized void log(BatchUpdateException ex) {
        if (ex == null) return;
        log.error(ex);
        int[] updateCount = ex.getUpdateCounts();
        boolean isShowData = updateCount != null && sqlQueries.size() == updateCount.length;
        if (!isShowData) return;
        for (int j = 0; j < updateCount.length; j++) {
            if (updateCount[j] == Statement.EXECUTE_FAILED) {
                log.error(sqlQueries.get(j));
            }
        }
    }
    
    public void add(String query) {
        sqlQueries.add(query);
    }

    public void clear() {
        sqlQueries.clear();
    }

    @Override
    public String toString() {
        return "BatchUpdateLog{" + "sqlQueries=" + sqlQueries + ", log=" + log + '}';
    }
}
