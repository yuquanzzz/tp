package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.academic.Academics;
import seedu.address.model.academic.Level;
import seedu.address.model.academic.Subject;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditAcademicsCommand.
 */
public class EditAcademicsCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_clearAcademics_success() {
        Person personInList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Academics academics = new Academics(new HashSet<>()); // clear

        EditAcademicsCommand editCommand = new EditAcademicsCommand(INDEX_FIRST_PERSON, academics);

        Person editedPerson = new PersonBuilder(personInList)
                .withAcademics(new Academics(new HashSet<>()))
                .build();

        String expectedMessage = String.format(
                EditAcademicsCommand.MESSAGE_EDIT_ACADEMICS_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personInList, editedPerson);

        CommandResult expectedCommandResult = new CommandResult(expectedMessage, editedPerson);
        assertCommandSuccess(editCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        Academics academics = new Academics(Set.of(new Subject("Math", Level.STRONG)));

        EditAcademicsCommand editCommand = new EditAcademicsCommand(outOfBoundIndex, academics);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        Academics academics = new Academics(Set.of(new Subject("Math", Level.STRONG)));

        final EditAcademicsCommand standardCommand =
                new EditAcademicsCommand(INDEX_FIRST_PERSON, academics);

        // same values -> returns true
        Academics copyAcademics = new Academics(Set.of(new Subject("Math", Level.STRONG)));
        EditAcademicsCommand commandWithSameValues =
                new EditAcademicsCommand(INDEX_FIRST_PERSON, copyAcademics);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(
                new EditAcademicsCommand(INDEX_SECOND_PERSON, academics)));

        // different academics -> returns false
        Academics differentAcademics = new Academics(Set.of(new Subject("Physics", Level.BASIC)));
        assertFalse(standardCommand.equals(
                new EditAcademicsCommand(INDEX_FIRST_PERSON, differentAcademics)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        Academics academics = new Academics(Set.of(new Subject("Math", Level.STRONG)));

        EditAcademicsCommand command = new EditAcademicsCommand(index, academics);

        String expected = EditAcademicsCommand.class.getCanonicalName()
                + "{index=" + index + ", academics=" + academics + "}";

        assertEquals(expected, command.toString());
    }
}
