package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Base command for operations that target a person from the filtered list by index.
 */
public abstract class IndexedPersonCommand extends Command {

    protected final Index index;

    protected IndexedPersonCommand(Index index) {
        requireNonNull(index);
        this.index = index;
    }

    /**
     * Returns the target person from the currently displayed person list.
     *
     * @throws CommandException if the index is out of bounds
     */
    protected Person getTargetPerson(Model model) throws CommandException {
        return IndexedPersonResolver.getTargetPerson(model, index);
    }

    /**
     * Replaces {@code personToEdit} with {@code editedPerson} while preserving the current filter.
     */
    protected void replacePerson(Model model, Person personToEdit, Person editedPerson) {
        requireNonNull(model);
        requireNonNull(personToEdit);
        requireNonNull(editedPerson);
        model.setPerson(personToEdit, editedPerson);
    }
}
