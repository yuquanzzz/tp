package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;

/**
 * Deletes fields of an entity identified using it's displayed index from the address book.
 */
public abstract class DeleteCommand extends IndexedPersonCommand {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes an entity identified by the indexes used in the displayed list.\n"
            + "Format: " + COMMAND_WORD + " SUBCOMMAND PARAMETERS\n"
            + "Examples: " + COMMAND_WORD + " student 1, " + COMMAND_WORD + " appt 1 2";

    public static final String MESSAGE_NOT_DELETED = "At least one field to delete must be provided.";

    protected DeleteCommand(Index index) {
        super(index);
    }
}
