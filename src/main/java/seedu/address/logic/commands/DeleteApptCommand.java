package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonBuilder;
import seedu.address.model.session.Appointment;

/**
 * Deletes an appointment from an existing person in the address book.
 */
public class DeleteApptCommand extends DeleteCommand {

    public static final String SUB_COMMAND_WORD = "appt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUB_COMMAND_WORD
            + ": Deletes the appointment identified by the appointment index from the student identified by the "
            + "person index used in the displayed student list.\n"
            + "Parameters: PERSON_INDEX APPT_INDEX (both must be positive integers)\n"
            + "Example: " + COMMAND_WORD + " " + SUB_COMMAND_WORD + " 1 2";

    public static final String MESSAGE_DELETE_APPT_SUCCESS = "Deleted appointment for %1$s: %2$s";
    public static final String MESSAGE_INVALID_APPOINTMENT_INDEX =
            "The appointment index provided is invalid for the selected student.";

    private static final DateTimeFormatter APPOINTMENT_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Index appointmentIndex;

    /**
     * Creates a {@code DeleteApptCommand}.
     */
    public DeleteApptCommand(Index personIndex, Index appointmentIndex) {
        super(personIndex);
        requireNonNull(appointmentIndex);
        this.appointmentIndex = appointmentIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Person personToEdit = getTargetPerson(model);
        Appointment appointmentToDelete = getTargetAppointment(personToEdit);

        List<Appointment> updatedAppointments = new ArrayList<>(personToEdit.getAppointments());
        updatedAppointments.remove(appointmentIndex.getZeroBased());

        Person editedPerson = new PersonBuilder(personToEdit)
                .withAppointments(updatedAppointments)
                .build();

        replacePerson(model, personToEdit, editedPerson);
        return new CommandResult(String.format(MESSAGE_DELETE_APPT_SUCCESS,
                Messages.format(editedPerson), formatAppointment(appointmentToDelete)), editedPerson);
    }

    private Appointment getTargetAppointment(Person person) throws CommandException {
        requireNonNull(person);
        List<Appointment> appointments = person.getAppointments();
        if (appointmentIndex.getZeroBased() >= appointments.size()) {
            throw new CommandException(MESSAGE_INVALID_APPOINTMENT_INDEX);
        }

        return appointments.get(appointmentIndex.getZeroBased());
    }

    private String formatAppointment(Appointment appointment) {
        return appointment.getStart().format(APPOINTMENT_DATE_TIME_FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteApptCommand)) {
            return false;
        }

        DeleteApptCommand otherCommand = (DeleteApptCommand) other;
        return index.equals(otherCommand.index)
                && appointmentIndex.equals(otherCommand.appointmentIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("personIndex", index)
                .add("appointmentIndex", appointmentIndex)
                .toString();
    }
}
