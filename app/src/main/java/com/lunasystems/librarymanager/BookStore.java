package com.lunasystems.librarymanager;

public interface BookStore {
    void putBook(Book b);

    Book getBook(String title);
}
