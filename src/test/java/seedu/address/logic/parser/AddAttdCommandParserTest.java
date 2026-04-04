package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ATTENDANCE_DATE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.ATTENDANCE_DATE_TIME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ATTENDANCE_DATE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ATTENDANCE_DATE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ATTENDANCE_DATE_TIME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddAttdCommand;

public class AddAttdCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddAttdCommand.MESSAGE_USAGE);

    private final AddAttdCommandParser parser = new AddAttdCommandParser();

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "y", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        assertParseFailure(parser, "-1 s/1 y", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0 s/1 y", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 s/0 y", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 maybe s/1", AddAttdCommandParser.MESSAGE_INVALID_ATTENDANCE_STATUS);
        assertParseFailure(parser, "1 s/1 y extra", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidDate_failure() {
        assertParseFailure(parser, "1 y s/1" + INVALID_ATTENDANCE_DATE_DESC,
                AddAttdCommandParser.MESSAGE_INVALID_ATTENDANCE_DATE_OR_TIME);
    }

    @Test
    public void parse_dateWithAbsence_failure() {
        assertParseFailure(parser, "1 n s/1" + ATTENDANCE_DATE_DESC,
                AddAttdCommandParser.MESSAGE_DATE_NOT_ALLOWED_FOR_ABSENCE);
    }

    @Test
    public void parse_presentWithoutDate_success() {
        Index targetPersonIndex = INDEX_FIRST_PERSON;
        Index targetAppointmentIndex = INDEX_FIRST_PERSON;
        assertParseSuccess(parser, "1 y s/1",
                new AddAttdCommand(targetPersonIndex, targetAppointmentIndex, true, Optional.empty()));
    }

    @Test
    public void parse_defaultPresentWithoutDate_success() {
        Index targetPersonIndex = INDEX_FIRST_PERSON;
        Index targetAppointmentIndex = INDEX_FIRST_PERSON;
        assertParseSuccess(parser, "1 s/1",
                new AddAttdCommand(targetPersonIndex, targetAppointmentIndex, true, Optional.empty()));
    }

    @Test
    public void parse_defaultPresentWithDate_success() {
        Index targetPersonIndex = INDEX_FIRST_PERSON;
        Index targetAppointmentIndex = INDEX_FIRST_PERSON;
        assertParseSuccess(parser, "1 s/1" + ATTENDANCE_DATE_DESC,
                new AddAttdCommand(targetPersonIndex, targetAppointmentIndex, true,
                        Optional.of(LocalDateTime.parse(VALID_ATTENDANCE_DATE + "T00:00:00"))));
    }

    @Test
    public void parse_presentWithDate_success() {
        Index targetPersonIndex = INDEX_FIRST_PERSON;
        Index targetAppointmentIndex = INDEX_FIRST_PERSON;
        assertParseSuccess(parser, "1 y s/1" + ATTENDANCE_DATE_DESC,
                new AddAttdCommand(targetPersonIndex, targetAppointmentIndex, true,
                        Optional.of(LocalDateTime.parse(VALID_ATTENDANCE_DATE + "T00:00:00"))));
    }

    @Test
    public void parse_presentWithDateTime_success() {
        Index targetPersonIndex = INDEX_FIRST_PERSON;
        Index targetAppointmentIndex = INDEX_FIRST_PERSON;
        assertParseSuccess(parser, "1 y s/1" + ATTENDANCE_DATE_TIME_DESC,
                new AddAttdCommand(targetPersonIndex, targetAppointmentIndex, true,
                        Optional.of(LocalDateTime.parse(VALID_ATTENDANCE_DATE_TIME))));
    }

    @Test
    public void parse_absentWithoutDate_success() {
        Index targetPersonIndex = INDEX_FIRST_PERSON;
        Index targetAppointmentIndex = INDEX_FIRST_PERSON;
        assertParseSuccess(parser, "1 n s/1",
                new AddAttdCommand(targetPersonIndex, targetAppointmentIndex, false, Optional.empty()));
    }
}
