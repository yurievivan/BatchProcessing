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

    }
}
