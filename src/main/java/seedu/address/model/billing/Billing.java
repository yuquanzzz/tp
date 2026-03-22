package seedu.address.model.billing;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;

import seedu.address.model.recurrence.Recurrence;

/**
 * Represents billing configuration for a tutee.
 * Guarantees: immutable.
 */
public class Billing {

    private static final double DEFAULT_TUITION_FEE = 0.0;
    private static final LocalDate DEFAULT_PAYMENT_DUE_DATE = LocalDate.now().withDayOfMonth(1);

    private final Recurrence recurrence;
    private final LocalDate paymentDueDate;
    private final double tuitionFee;

    /**
     * Creates a billing record with a tuition fee
     * @param tuitionFee A non-negative amount
     */
    public Billing(Recurrence recurrence, LocalDate paymentDueDate, double tuitionfee) {
        requireNonNull(recurrence);
        requireNonNull(paymentDueDate);
        checkArgument(tuitionfee >= 0, "Tuition fee must be non-negative");
        this.recurrence = recurrence;
        this.paymentDueDate = paymentDueDate;
        this.tuitionFee = tuitionfee;
    }

    /**
     * Returns a new {@code Billing} object with default monthly rate
     * @return {@code Billing} object with default monthly rate
     */
    public static Billing defaultBilling() {
        return new Billing(
                Recurrence.MONTHLY,
                DEFAULT_PAYMENT_DUE_DATE,
                DEFAULT_TUITION_FEE);
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public LocalDate getLastDueDate() {
        return paymentDueDate;
    }

    public double getTuitionFee() {
        return tuitionFee;
    }

    public LocalDate getNextDueDate() {
        return paymentDueDate.plusDays(recurrence.getDays());
    }

    /**
     * Updates payment due date to the target due date.
     * @param newDueDate Target due date
     * @return new {@code Billing} with updated due date
     */
    public Billing updatePaymentDueDate(LocalDate newDueDate) {
        requireNonNull(newDueDate);
        return new Billing(recurrence, newDueDate, tuitionFee);
    }

    /**
     * Advances the payment due date by one recurrence cycle.
     * Called when payment is recorded to move to next billing period.
     * @return new {@code Billing} with advanced due date
     */
    public Billing advanceDueDate() {
        return new Billing(recurrence, getNextDueDate(), tuitionFee);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Billing)) {
            return false;
        }
        Billing otherBilling = (Billing) other;
        return recurrence.equals(otherBilling.recurrence)
                && paymentDueDate.equals(otherBilling.paymentDueDate)
                && Double.compare(tuitionFee, otherBilling.tuitionFee) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(recurrence, paymentDueDate, tuitionFee);
    }

    @Override
    public String toString() {
        return String.format(
                "Billing[recurrence=%s, paymentDueDate=%s, tuitionFee=%.2f]",
                recurrence, paymentDueDate, tuitionFee);
    }
}

