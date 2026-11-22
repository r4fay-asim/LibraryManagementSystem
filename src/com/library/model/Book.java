package com.library.model;

/**
 * Represents a single book entity in the Library Book Management System,
 * storing its unique ID, title, author, and circulation status.
 *
 * @author Rafay Asim, Eesha Siddiqui
 */
public class Book {
    private final String id;
    private String title;
    private String author;
    private BookStatus status;

    /**
     * Constructs a new Book with its status initialized to {@link BookStatus#AVAILABLE}.
     *
     * @param id     unique identifier for the book
     * @param title  title of the book
     * @param author author of the book
     */
    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = BookStatus.AVAILABLE;
    }

    /**
     * Returns the unique identifier of the book.
     *
     * @return book ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the title of the book.
     *
     * @return book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Updates the title of the book.
     *
     * @param title new book title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the author of the book.
     *
     * @return book author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Updates the author of the book.
     *
     * @param author new book author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the current circulation status of the book.
     *
     * @return current {@link BookStatus}
     */
    public BookStatus getStatus() {
        return status;
    }

    /**
     * Updates the circulation status of the book.
     *
     * @param status new {@link BookStatus}
     */
    public void setStatus(BookStatus status) {
        this.status = status;
    }
}