package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteApptCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeletePersonCommand;

/**
 * Contains parser tests for delete subcommands.
 */
public class DeletePersonCommandParserTest {

    private final DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validStudentArgs_returnsDeletePersonCommand() {
        assertParseSuccess(parser, "student 1", new DeletePersonCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_validApptArgs_returnsDeleteApptCommand() {
        assertParseSuccess(parser, "appt 1 s/2", new DeleteApptCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
    }

    @Test
    public void parse_invalidStudentArgs_throwsParseException() {
        assertParseFailure(parser, "student a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePersonCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidApptArgs_throwsParseException() {
        assertParseFailure(parser, "appt 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteApptCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "appt 1 s/a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteApptCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_unknownSubcommand_throwsParseException() {
        assertParseFailure(parser, "unknown 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }
}
