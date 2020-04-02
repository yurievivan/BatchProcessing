package jdbc.batch;

import dao.batch.Dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ivan.yuriev
 */
public class BookDaoPreparedStatement extends BatchExecutor implements Dao<Book> {

    private static final Logger LOG = Logger.getLogger(BookDaoPreparedStatement.class);
    private static final int BATCH_SIZE = 50;
    private static final String QUERY = "Query:";

    public BookDaoPreparedStatement() {
        super(LOG);
    }

    private enum Query {
        SELECT_ALL("SELECT * FROM book"),
        SELECT_BY_ID("SELECT * FROM book WHERE id = ?"),
        INSERT("INSERT INTO book (title) VALUES (?)"),
        UPDATE("UPDATE book SET title = ? WHERE id = ?"),
        DELETE_BY_ID("DELETE FROM book WHERE id = ?"),
        DELETE_BY_TITLE("DELETE FROM book WHERE title = ?"),
        DROP_TABLE("DROP table if exists book"),
        CREATE_TABLE("create table book ( "
                + " id  bigserial not null, "
                + " title varchar(50), "
                + " primary key (id) "
                + ")");

        private final String sql;

        Query(String sql) {
            this.sql = sql;
        }

        public String getSql() {
            return sql;
        }
    }

    @Override
    public Book getBook(long id) {
        try (Connection connection = DbConnection.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.SELECT_BY_ID.getSql())) {
                preparedStatement.setLong(1, id);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        Book book = new Book();
                        book.setId(rs.getLong("id"));
                        book.setTitle(rs.getString("title"));
                        return book;
                    }
                }
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return null;
    }

    @Override
    public List<Book> getAllBooks() {
        try (Connection connection = DbConnection.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.SELECT_ALL.getSql())) {
               try (ResultSet rs = preparedStatement.executeQuery()) {
                   List<Book> books = new ArrayList<>();
                   while (rs.next()) {
                       Book book = new Book();
                       book.setId(rs.getLong("id"));
                       book.setTitle(rs.getString("title"));
                       books.add(book);
                   }
                   return books;
               }
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        }
        return Collections.emptyList();
    }

    @Override
    public void insertBooks(List<Book> books) {
        addBatchQueries(Query.INSERT, books);
    }

    @Override
    public void updateBooks(List<Book> books) {
        addBatchQueries(Query.UPDATE, books);
    }

    @Override
    public void deleteBooks(List<Book> books) {
        List<Book> booksWithId = new ArrayList<>();
        List<Book> booksWithTitle = new ArrayList<>();
        for (Book book : books) {
            if (book.getId() > 0) {
                booksWithId.add(book);
            } else {
                booksWithTitle.add(book);
            }
        }
        addBatchQueries(Query.DELETE_BY_ID, booksWithId);
        addBatchQueries(Query.DELETE_BY_TITLE, booksWithTitle);
    }

    @Override
    public void dropTable() {
        executeQuery(Query.DROP_TABLE);
    }

    @Override
    public void createTable() {
        executeQuery(Query.CREATE_TABLE);
    }
    
    private void executeQuery(Query query) {
        try (Connection connection = DbConnection.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query.getSql())) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        }
    }

    private void addBatchQueries(Query query, List<Book> books) {
        if (books == null || books.isEmpty()) return;
        try (Connection connection = DbConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement pstmt = connection.prepareStatement(query.getSql())) {
                for (int i = 0; i < books.size(); i++) {
                    getBatchUpdateLog().add(initPrepareStatement(query, pstmt, books.get(i)));
                    pstmt.addBatch();

                    if (((i + 1) % BATCH_SIZE == 0) || (i == (books.size() - 1))) {
                        executeBatch(connection, pstmt);
                    }
                }
            }
        } catch (SQLException ex) {
            LOG.error(ex);
        } finally {
            getBatchUpdateLog().clear();
        }
    }

    private String initPrepareStatement(Query query, PreparedStatement pstmt, Book book) throws SQLException {
        String sqlArgs = "";
        switch (query) {
            case INSERT:
            case DELETE_BY_TITLE:
                pstmt.setString(1, book.getTitle());
                sqlArgs = String.join(" ", QUERY, query.getSql(), "Title:", book.getTitle());
                break;

            case UPDATE:
                pstmt.setString(1, book.getTitle());
                pstmt.setLong(2, book.getId());
                sqlArgs = String.join(" ", QUERY, query.getSql(), "Title:", book.getTitle(), "id:", String.valueOf(book.getId()));
                break;

            case DELETE_BY_ID:
                pstmt.setLong(1, book.getId());
                sqlArgs = String.join(" ", QUERY, query.getSql(), "id:", String.valueOf(book.getId()));
                break;
            default:
                throw new IllegalStateException("Unknown Query.");    
        }
        return sqlArgs;
    }
}
