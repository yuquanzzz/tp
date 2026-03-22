package seedu.address.model.recurrence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class RecurrenceTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2026, 1, 1);

    @Test
    public void getDays_monthly_returns30() {
        assertEquals(30, Recurrence.MONTHLY.getDays());
    }

    @Test
    public void getDays_none_returns0() {
        assertEquals(0, Recurrence.NONE.getDays());
    }

    @Test
    public void nextDueDate_monthly_returnsDatePlus30Days() {
        LocalDate expected = TEST_DATE.plusDays(30);
        assertEquals(expected, Recurrence.MONTHLY.nextDueDate(TEST_DATE));
    }

    @Test
    public void nextDueDate_none_returnsNull() {
        assertNull(Recurrence.NONE.nextDueDate(TEST_DATE));
    }

    @Test
    public void nextDueDate_nullDate_returnsNull() {
        assertNull(Recurrence.MONTHLY.nextDueDate(null));
    }
}
