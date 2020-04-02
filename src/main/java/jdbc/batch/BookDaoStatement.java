package jdbc.batch;

import dao.batch.Dao;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ivan.yuriev
 */
public class BookDaoStatement extends BatchExecutor implements Dao<Book> {

    private static final Logger LOG = Logger.getLogger(BookDaoStatement.class);
    private final static int BATCH_SIZE = 50;
    private final static String SELECT_ALL = "SELECT * FROM book";
    private final static String SELECT_BY_ID = "SELECT * FROM book WHERE id = %d";
    private final static String INSERT = "INSERT INTO book (title) VALUES ( '%s')";
    private final static String UPDATE = "UPDATE book SET title = '%s' WHERE id = %d";
    private final static String DELETE_BY_ID = "DELETE FROM book WHERE id = %d";
    private final static String DELETE_BY_TITLE = "DELETE FROM book WHERE title = '%s'";
    private final static String DROP_TABLE = "DROP table if exists book";
    private final static String CREATE_TABLE = "create table book ( "
            + " id  bigserial not null, "
            + " title varchar(50), "
            + " primary key (id) "
            + ")";

    public BookDaoStatement() {
        super(LOG);
    }

    @Override
    public Book getBook(long id) {
        try (Connection connection = DbConnection.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(SELECT_BY_ID, id));
            if (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("id"));
                book.setTitle(rs.getString("title"));
                return book;
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return null;
    }

    @Override
    public List<Book> getAllBooks() {
        try (Connection connection = DbConnection.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL);
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("id"));
                book.setTitle(rs.getString("title"));
                books.add(book);
            }
            return books;
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return null;
    }

    @Override
    public void insertBooks(List<Book> books) {
        List<String> queries = new ArrayList<>();
        books.stream().forEach((book) -> {
            queries.add(String.format(INSERT, book.getTitle()));
        });
        addBatchQueries(queries);
    }

    @Override
    public void updateBooks(List<Book> books) {
        List<String> queries = new ArrayList<>();
        books.stream().forEach((book) -> {
            queries.add(String.format(UPDATE, book.getTitle(), book.getId()));
        });
        addBatchQueries(queries);
    }

    @Override
    public void deleteBooks(List<Book> books) {
        List<String> queries = new ArrayList<>();
        for (Book book : books) {
            String query = book.getId() > 0 ? String.format(DELETE_BY_ID, book.getId())
                    : String.format(DELETE_BY_TITLE, book.getTitle());
            queries.add(query);
        }
        addBatchQueries(queries);
    }

    @Override
    public void dropTable() {
        executeQuery(DROP_TABLE);
    }

    @Override
    public void createTable() {
        executeQuery(CREATE_TABLE);
    }
    
    private void executeQuery(String query) {
        try (Connection connection = DbConnection.getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeQuery(query);
        } catch (SQLException ex) {
            LOG.error(ex);
        }
    }

    private void addBatchQueries(List<String> queries) {
        try (Connection connection = DbConnection.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                connection.setAutoCommit(false);
                for (int i = 0; i < queries.size(); i++) {
                    stmt.addBatch(queries.get(i));
                    getBatchUpdateLog().add(queries.get(i));
                    if (((i + 1) % BATCH_SIZE == 0) || (i == (queries.size() - 1))) {
                        executeBatch(connection, stmt);
                    }
                }
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        } finally {
            getBatchUpdateLog().clear();
        }
    }

}
