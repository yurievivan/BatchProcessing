package dao.batch;

import java.util.List;

public interface Dao<T> {

    T getBook(long id);

    List<T> getAllBooks();

    void insertBooks(List<T> books);

    void updateBooks(List<T> books);

    void deleteBooks(List<T> books);

    void dropTable();

    void createTable();
}
