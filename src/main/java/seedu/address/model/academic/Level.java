package seedu.address.model.academic;

/**
 * Represents the level of a Subject.
 */
public enum Level {
    BASIC,
    STRONG;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
