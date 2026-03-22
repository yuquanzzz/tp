package seedu.address.model.recurrence;

import java.time.LocalDate;

/**
 * Recurrence schedule options for tuition payment.
 */
public enum Recurrence {
    WEEKLY(7),
    BIWEEKLY(14),
    MONTHLY(30),
    NONE(0);

    private final int days;

    /**
     * Creates a {@code Recurrence} object with specified days
     *      to next payment
     * @param days A valid integer
     */
    Recurrence(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

    /**
     * Returns next due date
     * @param lastPaidDate A valid date
     * @return next due date
     */
    public LocalDate nextDueDate(LocalDate lastPaidDate) {
        if (lastPaidDate == null || this == NONE) {
            return null;
        }
        return lastPaidDate.plusDays(days);
    }
}
