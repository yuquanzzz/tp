package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddPaymentCommand;

public class AddPaymentCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddPaymentCommand.MESSAGE_USAGE);
    private static final String VALID_DATE = "2026-01-13";
    private static final String VALID_DATE_DESC = " " + PREFIX_DATE + VALID_DATE;

    private final AddPaymentCommandParser parser = new AddPaymentCommandParser();

    @Test
    public void parse_missingDate_failure() {
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        assertParseFailure(parser, "-1" + VALID_DATE_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0" + VALID_DATE_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 abc" + VALID_DATE_DESC, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidDate_failure() {
        assertParseFailure(parser, "1 " + PREFIX_DATE + "2026-13-40", ParserUtil.MESSAGE_INVALID_DATE);
    }
}
