package com.library.service;

import com.library.model.Book;
import com.library.model.BookStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafay Asim, Eesha Siddiqui
 */
public class LibraryManager {
    private final Map<String, Book> catalog;

    /**
     * Constructs an empty LibraryManager backed by an insertion-ordered map.
     */
    public LibraryManager() {
        this.catalog = new LinkedHashMap<>();
    }

    /**
     * Validates book attributes and adds a new book to the library.
     *
     * @param id     unique identifier for the book (cannot be empty or duplicate)
     * @param title  title of the book (cannot be empty)
     * @param author author of the book (cannot be empty)
     * @throws IllegalArgumentException if any field is blank or the ID already exists
     */
    public void addBook(String id, String title, String author) {
        validateFields(id, title, author);

        String key = id.trim().toLowerCase();
        if (catalog.containsKey(key)) {
            throw new IllegalArgumentException(
                    "Duplicate ID: A book with ID '" + id.trim() + "' already exists."
            );
        }

        catalog.put(key, new Book(id.trim(), title.trim(), author.trim()));
    }

    /**
     * Returns a list of all books currently stored in the library.
     *
     * @return list of all {@link Book} records in insertion order
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(catalog.values());
    }

    /**
     * Checks whether the library catalog currently contains no books.
     *
     * @return {@code true} if the library is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return catalog.isEmpty();
    }

    /**
     * Updates the title, author, and circulation status of an existing book.
     *
     * @param id        ID of the existing book to update
     * @param newTitle  updated book title
     * @param newAuthor updated book author
     * @param newStatus updated {@link BookStatus}
     * @throws IllegalArgumentException if fields are blank, status is null, or ID is not found
     */
    public void updateBook(String id, String newTitle, String newAuthor, BookStatus newStatus) {
        validateFields(id, newTitle, newAuthor);
        if (newStatus == null) {
            throw new IllegalArgumentException("Validation Error: Please select a valid book status.");
        }

        Book existingBook = findBookById(id);
        existingBook.setTitle(newTitle.trim());
        existingBook.setAuthor(newAuthor.trim());
        existingBook.setStatus(newStatus);
    }

    /**
     * Updates only the circulation status of an existing book.
     *
     * @param id        ID of the existing book
     * @param newStatus new {@link BookStatus} (Available, Issued, or Reserved)
     * @throws IllegalArgumentException if the ID is blank, status is null, or book is not found
     */
    public void setBookStatus(String id, BookStatus newStatus) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Book ID cannot be empty.");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("Validation Error: Please select a valid status.");
        }

        Book existingBook = findBookById(id);
        existingBook.setStatus(newStatus);
    }

    /**
     * Removes a book from the library by its ID.
     *
     * @param id ID of the book to delete
     * @throws IllegalArgumentException if the ID is blank or no matching book exists
     */
    public void deleteBook(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Book ID cannot be empty.");
        }

        String key = id.trim().toLowerCase();
        if (!catalog.containsKey(key)) {
            throw new IllegalArgumentException(
                    "Delete Error: No book found with ID '" + id.trim() + "'."
            );
        }
        catalog.remove(key);
    }

    /**
     * Validates that ID, title, and author are neither null nor empty.
     *
     * @param id     book ID to validate
     * @param title  book title to validate
     * @param author book author to validate
     */
    private void validateFields(String id, String title, String author) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Book ID cannot be empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Book Title cannot be empty.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Book Author cannot be empty.");
        }
    }

    /**
     * Looks up an existing book by ID or throws an exception if it does not exist.
     *
     * @param id book ID to locate
     * @return matching {@link Book} instance
     */
    private Book findBookById(String id) {
        Book book = catalog.get(id.trim().toLowerCase());
        if (book == null) {
            throw new IllegalArgumentException(
                    "Lookup Error: No book found with ID '" + id.trim() + "'."
            );
        }
        return book;
    }
}