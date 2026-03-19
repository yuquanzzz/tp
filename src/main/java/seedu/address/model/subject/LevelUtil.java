package seedu.address.model.subject;

/**
 * Utility class for parsing and validating {@link Level}.
 */
public class LevelUtil {

    public static final String MESSAGE_CONSTRAINTS =
            "Level must be either 'basic' or 'strong' (case-insensitive).";

    /**
     * Parses a string into a {@code Level}.
     *
     * @param input input string
     * @return corresponding Level
     * @throws IllegalArgumentException if invalid
     */
    public static Level levelFromString(String input) {
        if (input == null) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }

        String normalized = input.trim().toLowerCase();

        switch (normalized) {
        case "basic":
            return Level.BASIC;
        case "strong":
            return Level.STRONG;
        default:
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
    }
}
