package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditAcademicsCommand;
import seedu.address.logic.commands.EditAcademicsCommand.EditAcademicsDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.academic.Level;
import seedu.address.model.academic.LevelUtil;
import seedu.address.model.academic.Subject;

/**
 * Parses input arguments and creates a new {@code EditAcademicsCommand}.
 *
 * Logic:
 * - Parse dsc/ first (order independent)
 * - Remove dsc/ segment
 * - Parse s/ l/ strictly
 * - Only overwrite fields that are present
 */
public class EditAcademicsCommandParser implements Parser<EditAcademicsCommand> {

    @Override
    public EditAcademicsCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String trimmed = args.trim();
        if (trimmed.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            EditAcademicsCommand.MESSAGE_USAGE));
        }

        // ================= INDEX =================
        String[] split = trimmed.split("\\s+", 2);
        Index index;

        try {
            index = ParserUtil.parseIndex(split[0]);
        } catch (ParseException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            EditAcademicsCommand.MESSAGE_USAGE), e);
        }

        if (split.length == 1) {
            throw new ParseException(
                    "At least one field (subjects or note) must be provided.");
        }

        String remainder = split[1];
        EditAcademicsDescriptor descriptor = new EditAcademicsDescriptor();

        // ================= PARSE dsc/ FIRST =================
        String subjectLevelPart = remainder;

        int dscIndex = remainder.indexOf(PREFIX_DESCRIPTION.getPrefix());
        if (dscIndex != -1) {

            int start = dscIndex + PREFIX_DESCRIPTION.getPrefix().length();

            // Find next prefix (s/ or l/)
            int nextSubject = remainder.indexOf(PREFIX_SUBJECT.getPrefix(), start);
            int nextLevel = remainder.indexOf(PREFIX_LEVEL.getPrefix(), start);

            int end = remainder.length();

            if (nextSubject != -1 && nextSubject < end) {
                end = nextSubject;
            }
            if (nextLevel != -1 && nextLevel < end) {
                end = nextLevel;
            }

            String note = remainder.substring(start, end).trim();
            descriptor.setNote(note);

            // Remove dsc segment cleanly
            String before = remainder.substring(0, dscIndex).trim();
            String after = (end < remainder.length())
                    ? remainder.substring(end).trim()
                    : "";

            subjectLevelPart = (before + " " + after).trim();
        }

        // ================= PARSE s/ l/ =================
        if (!subjectLevelPart.isEmpty()) {

            List<Subject> subjects = new ArrayList<>();
            Subject current = null;

            String[] tokens = subjectLevelPart.split("\\s+");

            for (String token : tokens) {

                if (token.startsWith(PREFIX_SUBJECT.getPrefix())) {

                    String name = token.substring(PREFIX_SUBJECT.getPrefix().length());

                    if (!Subject.isValidSubjectName(name)) {
                        throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
                    }

                    current = new Subject(name, null);
                    subjects.add(current);

                } else if (token.startsWith(PREFIX_LEVEL.getPrefix())) {

                    if (current == null) {
                        throw new ParseException("Level must follow a subject.");
                    }

                    if (current.getLevel().isPresent()) {
                        throw new ParseException("Each subject can only have one level.");
                    }

                    String levelStr = token.substring(PREFIX_LEVEL.getPrefix().length());

                    Level level;
                    try {
                        level = LevelUtil.levelFromString(levelStr);
                    } catch (IllegalArgumentException e) {
                        throw new ParseException(LevelUtil.MESSAGE_CONSTRAINTS);
                    }

                    // Replace last subject with level
                    subjects.remove(subjects.size() - 1);
                    current = new Subject(current.getName(), level);
                    subjects.add(current);

                } else {
                    throw new ParseException(
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                                    EditAcademicsCommand.MESSAGE_USAGE));
                }
            }

            // Duplicate check
            Set<String> seen = new HashSet<>();
            for (Subject s : subjects) {
                if (!seen.add(s.getName())) {
                    throw new ParseException("Duplicate subjects are not allowed.");
                }
            }

            descriptor.setSubjects(new HashSet<>(subjects));
        }

        // ================= FINAL VALIDATION =================
        if (!descriptor.isSubjectsEdited() && !descriptor.isNoteEdited()) {
            throw new ParseException(
                    "At least one field (subjects or note) must be provided.");
        }

        return new EditAcademicsCommand(index, descriptor);
    }
}
