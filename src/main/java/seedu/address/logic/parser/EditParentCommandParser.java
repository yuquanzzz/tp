package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_PHONE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditParentCommand;
import seedu.address.logic.commands.EditParentCommand.EditParentDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code EditParentCommand} object.
 */
public class EditParentCommandParser implements Parser<EditParentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditParentCommand
     * and returns an EditParentCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public EditParentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PARENT_NAME, PREFIX_PARENT_PHONE,
                PREFIX_PARENT_EMAIL);

        Index index = ParserUtil.parseIndex(argMultimap.getPreamble(), EditParentCommand.MESSAGE_USAGE);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_PARENT_NAME, PREFIX_PARENT_PHONE, PREFIX_PARENT_EMAIL);

        EditParentDescriptor editParentDescriptor = new EditParentDescriptor();

        if (argMultimap.getValue(PREFIX_PARENT_NAME).isPresent()) {
            editParentDescriptor.setParentName(ParserUtil.parseParentName(argMultimap.getValue(PREFIX_PARENT_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PARENT_PHONE).isPresent()) {
            editParentDescriptor.setParentPhone(ParserUtil.parseParentPhone(argMultimap.getValue(PREFIX_PARENT_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_PARENT_EMAIL).isPresent()) {
            editParentDescriptor.setParentEmail(ParserUtil.parseParentEmail(argMultimap.getValue(PREFIX_PARENT_EMAIL).get()));
        }

        if (!editParentDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditParentCommand(index, editParentDescriptor);
    }
}
