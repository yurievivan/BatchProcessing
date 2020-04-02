package jdbc.batch;

import dao.batch.Dao;
import java.util.ArrayList;
import java.util.List;

public class App {

    /**
     *
     * @author ivan.yuriev
     */
    public static void main(String[] args) {

        Dao dao = new BookDaoPreparedStatement();
        //Dao dao =  new BookDaoStatement();
        dao.dropTable();
        dao.createTable();
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            String title = String.join("", "JDBC Insert Example: ", String.valueOf(i));
            Book book = new Book();
            book.setTitle(title);
            books.add(book);
        }

        dao.insertBooks(books);

        books = dao.getAllBooks();
        for (int i = 0; i < books.size(); i++) {
            String title = String.join("", "JDBC Update Example: ", String.valueOf(i));
            books.get(i).setTitle(title);
        }
        dao.updateBooks(books);

        //CallStoredProcedure callProc = new CallStoredProcedure();
        //callProc.runStoredProcedure(books);
    }
}