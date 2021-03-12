package seedu.address.model.order.exceptions;

/**
 * Signals that the operation will result in duplicate Dishes (Dishes are considered duplicates if they have the same
 * identity).
 */
public class DuplicateOrderException extends RuntimeException {
    public DuplicateOrderException() {
        super("Operation would result in duplicate persons");
    }
}
