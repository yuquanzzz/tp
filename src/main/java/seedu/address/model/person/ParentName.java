package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's parent name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidParentName(String)}
 */
public class ParentName {

    public static final String MESSAGE_CONSTRAINTS =
            "Parent names should only contain alphanumeric characters and spaces, and it should not be blank";

    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String fullName;

    /**
     * Constructs a {@code ParentName}.
     *
     * @param name A valid parent name.
     */
    public ParentName(String name) {
        requireNonNull(name);
        checkArgument(isValidParentName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid parent name.
     */
    public static boolean isValidParentName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ParentName)) {
            return false;
        }

        ParentName otherParentName = (ParentName) other;
        return fullName.equals(otherParentName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
