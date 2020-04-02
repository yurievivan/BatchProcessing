package hibernate.batch;

import dao.batch.Dao;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String... args) throws InterruptedException {
        
        Dao dao = new BookDao();
        List<Book> books = new ArrayList<>();
        
        // - - - - - - - - - - - - - - Hibernate/JPA Batch Insert example - - - - - - - - - - - -
        for (int i = 0; i < 755; i++) {
            Book book = new Book("Hibernate Insert Example: " + i);
            books.add(book);
        }
        dao.insertBooks(books);

        // - - - - - - - - - - - - - - Hibernate/JPA Batch Update example - - - - - - - - - - - -
        books = (List<Book>) dao.getAllBooks();
        for (int i = 0; i < books.size(); i++) {
            books.get(i).setTitle("Hibernate Update Example: " + i);
        }
        dao.updateBooks(books);
        
        System.out.println(dao.getBook(50L));
        
        /*

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        // - - - - - - - - - - - - - - Hibernate/JPA Batch Insert example - - - - - - - - - - - -
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        transaction = session.getTransaction();
        transaction.begin();
        for (int i = 0; i < 1000; i++) {
            Book book = new Book("Hibernate/JPA Batch Insert Example: " + i);
            session.persist(book);

            if ((i+1) % BATCH_SIZE == 0) {
                // Flush and clear the cache every batch
                session.flush();
                session.clear();
            }
        }
        transaction.commit();

        // - - - - - - - - - - - - - - Hibernate/JPA Batch Update example - - - - - - - - - - - -
        session = sessionFactory.getCurrentSession();
        transaction = session.getTransaction();
        transaction.begin();
        List<Book> books = session.createQuery("From Book", Book.class).getResultList();

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            book.setTitle("Hibernate/JPA Batch Update Example: " + i);
            session.update(book);

            if ((i+1) % BATCH_SIZE == 0) {
                // Flush and clear the cache every batch
                session.flush();
                session.clear();
            }
        }

        transaction.commit();*/
    }
}
