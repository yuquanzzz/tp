package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_SUBJECT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.SUBJECT_DESC_MATH_STRONG;
import static seedu.address.logic.commands.CommandTestUtil.SUBJECT_DESC_SCIENCE_BASIC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditAcademicsCommand;
import seedu.address.logic.commands.EditAcademicsCommand.EditAcademicsDescriptor;
import seedu.address.model.academic.Level;
import seedu.address.model.academic.Subject;

/**
 * Tests for EditAcademicsCommandParser.
 */
public class EditAcademicsCommandParserTest {

    private static final String SUBJECT_EMPTY = " " + PREFIX_SUBJECT;

    private static final String MESSAGE_INVALID_SUBJECT_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditAcademicsCommand.MESSAGE_USAGE);

    private EditAcademicsCommandParser parser = new EditAcademicsCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index
        assertParseFailure(parser, SUBJECT_DESC_MATH_STRONG, MESSAGE_INVALID_SUBJECT_FORMAT);

        // empty input
        assertParseFailure(parser, "", MESSAGE_INVALID_SUBJECT_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        assertParseFailure(parser, "-5" + SUBJECT_DESC_MATH_STRONG, MESSAGE_INVALID_SUBJECT_FORMAT);
        assertParseFailure(parser, "0" + SUBJECT_DESC_MATH_STRONG, MESSAGE_INVALID_SUBJECT_FORMAT);
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_SUBJECT_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_SUBJECT_DESC, Subject.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_singleSubjectWithLevel_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + SUBJECT_DESC_MATH_STRONG;

        EditAcademicsDescriptor descriptor = new EditAcademicsDescriptor();
        descriptor.setSubjects(Set.of(new Subject("Math", Level.STRONG)));

        EditAcademicsCommand expectedCommand =
                new EditAcademicsCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleSubjects_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased()
                + SUBJECT_DESC_MATH_STRONG
                + SUBJECT_DESC_SCIENCE_BASIC;

        EditAcademicsDescriptor descriptor = new EditAcademicsDescriptor();
        descriptor.setSubjects(Set.of(
                new Subject("Math", Level.STRONG),
                new Subject("Science", Level.BASIC)
        ));

        EditAcademicsCommand expectedCommand =
                new EditAcademicsCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_subjectWithoutLevel_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " s/Math";

        EditAcademicsDescriptor descriptor = new EditAcademicsDescriptor();
        descriptor.setSubjects(Set.of(new Subject("Math", null)));

        EditAcademicsCommand expectedCommand =
                new EditAcademicsCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_noteOnly_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " dsc/Good progress";

        EditAcademicsDescriptor descriptor = new EditAcademicsDescriptor();
        descriptor.setNote("Good progress");

        EditAcademicsCommand expectedCommand =
                new EditAcademicsCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_subjectAndNote_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased()
                + SUBJECT_DESC_MATH_STRONG
                + " dsc/Good";

        EditAcademicsDescriptor descriptor = new EditAcademicsDescriptor();
        descriptor.setSubjects(Set.of(new Subject("Math", Level.STRONG)));
        descriptor.setNote("Good");

        EditAcademicsCommand expectedCommand =
                new EditAcademicsCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_emptySubject_failure() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + SUBJECT_EMPTY;

        assertParseFailure(parser, userInput, Subject.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_noFields_failure() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = String.valueOf(targetIndex.getOneBased());

        assertParseFailure(parser, userInput,
                "At least one field (subjects or note) must be provided.");
    }
}
