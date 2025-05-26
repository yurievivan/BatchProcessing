package hibernate.batch;

import dao.batch.Dao;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;

/**
 *
 * @author ivan.yuriev
 */
public class BookDao implements Dao<Book> {

    private static final int BATCH_SIZE = 50;
    private static final String DROP_TABLE = "DROP table if exists book";
    private static final String CREATE_TABLE = "create table book ( "
            + " id  bigserial not null, "
            + " title varchar(50), "
            + " primary key (id) "
            + ")";

    private enum DML {
        INSERT,
        UPDATE,
        DELETE
    }

    private final boolean useStatelessSession;

    public BookDao(boolean useStatelessSession) {
        this.useStatelessSession = useStatelessSession;
    }

    @Override
    public Book getBook(long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Book book = session.find(Book.class, id);
        session.getTransaction().commit();
        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Book> books = session.createQuery("From Book", Book.class).list();
        session.getTransaction().commit();
        return books;
    }

    @Override
    public void insertBooks(List<Book> books) {
        if (useStatelessSession) {
            executeBatchWithStateless(DML.INSERT, books);
        } else {
            executeBatchQueries(DML.INSERT, books);
        }
    }

    @Override
    public void updateBooks(List<Book> books) {
        if (useStatelessSession) {
            executeBatchWithStateless(DML.UPDATE, books);
        } else {
            executeBatchQueries(DML.UPDATE, books);
        }
    }

    @Override
    public void deleteBooks(List<Book> books) {
        if (useStatelessSession) {
            executeBatchWithStateless(DML.DELETE, books);
        } else {
            executeBatchQueries(DML.DELETE, books);
        }
    }

    private void executeBatchQueries(DML dml, List<Book> books) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.getTransaction();
        transaction.begin();
        for (int i = 0; i < books.size(); i++) {
            switch (dml) {
                case INSERT -> session.persist(books.get(i));

                case UPDATE -> session.merge(books.get(i));

                case DELETE -> session.remove(books.get(i));
            }

            if ((i + 1) % BATCH_SIZE == 0) {
                // Flush and clear the cache every batch
                session.flush();
                session.clear();
            }
        }
        transaction.commit();
    }

    private void executeBatchWithStateless(DML dml, List<Book> books) {
        try (StatelessSession session = HibernateUtil.getSessionFactory().openStatelessSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            switch (dml) {
                case INSERT -> session.insertMultiple(books);

                case UPDATE -> session.updateMultiple(books);

                case DELETE -> session.deleteMultiple(books);
            }

            transaction.commit();
        }
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
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.createNamedMutationQuery(query).executeUpdate();
        session.getTransaction().commit();
    }

}
