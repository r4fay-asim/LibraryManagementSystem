package com.library.model;

/**
 * Defines the possible circulation statuses for a library book.
 *
 * @author Student One, Student Two
 */
public enum BookStatus {
    /** Book is available on the shelf for borrowing. */
    AVAILABLE("Available"),

    /** Book is currently issued to a borrower. */
    ISSUED("Issued"),

    /** Book is reserved and unavailable for general borrowing. */
    RESERVED("Reserved");

    private final String displayText;

    /**
     * Constructs a BookStatus constant with a human-readable label.
     *
     * @param displayText formatted status name
     */
    BookStatus(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Returns the display label of the status.
     *
     * @return formatted status string ({@code Available}, {@code Issued}, or {@code Reserved})
     */
    @Override
    public String toString() {
        return displayText;
    }
}