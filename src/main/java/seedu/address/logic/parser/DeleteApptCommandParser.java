package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code DeleteApptCommand} object.
 */
public class DeleteApptCommandParser implements Parser<DeleteApptCommand> {

    @Override
    public DeleteApptCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        String[] parts = trimmedArgs.split("\\s+");
        if (parts.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteApptCommand.MESSAGE_USAGE));
        }

        Index personIndex = ParserUtil.parseIndex(parts[0], DeleteApptCommand.MESSAGE_USAGE);
        Index appointmentIndex = ParserUtil.parseIndex(parts[1], DeleteApptCommand.MESSAGE_USAGE);
        return new DeleteApptCommand(personIndex, appointmentIndex);
    }
}
