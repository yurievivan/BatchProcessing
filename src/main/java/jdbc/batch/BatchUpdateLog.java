package jdbc.batch;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.Logger;

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

    public synchronized void log(SQLException ex) {
        if (ex == null) return;
        log.error(ex);
        sqlQueries.stream().forEach((query) -> {
            log.error(query);
        });
        sqlQueries.clear();
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
