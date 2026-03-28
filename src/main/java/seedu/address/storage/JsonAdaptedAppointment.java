package seedu.address.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.attendance.AttendanceRecords;
import seedu.address.model.recurrence.Recurrence;
import seedu.address.model.session.Appointment;

/**
 * Jackson-friendly version of {@link Appointment}.
 */
class JsonAdaptedAppointment {

    private static final String APPOINTMENT_START_MESSAGE_CONSTRAINTS =
            "Appointment start date-time must be in ISO 8601 local format, e.g. 2026-01-13T08:00:00";
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withResolverStyle(ResolverStyle.STRICT);

    private final String start;
    private final String next;
    private final String recurrence;
    private final String description;
    private final List<JsonAdaptedAppointmentAttendance> attendanceRecords;

    @JsonCreator
    public JsonAdaptedAppointment(@JsonProperty("start") String start,
                                  @JsonProperty("next") String next,
                                  @JsonProperty("recurrence") String recurrence,
                                  @JsonProperty("description") String description,
                                  @JsonProperty("attendanceRecords")
                                  List<JsonAdaptedAppointmentAttendance> attendanceRecords) {
        this.start = start;
        this.next = next;
        this.recurrence = recurrence;
        this.description = description;
        this.attendanceRecords = attendanceRecords;
    }

    public JsonAdaptedAppointment(Appointment source) {
        this.start = source.getStart().toString();
        this.next = source.getNext().toString();
        this.recurrence = source.getRecurrence().name();
        this.description = source.getDescription();
        this.attendanceRecords = source.getAttendance().getRecords().stream()
                .map(JsonAdaptedAppointmentAttendance::new)
                .toList();
    }

    public Appointment toModelType() throws IllegalValueException {
        if (start == null) {
            throw new IllegalValueException(String.format(
                    JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT, "appointment start"));
        }

        LocalDateTime modelStart;
        try {
            modelStart = LocalDateTime.parse(start, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalValueException(APPOINTMENT_START_MESSAGE_CONSTRAINTS);
        }

        LocalDateTime modelNext = modelStart;
        if (next != null) {
            try {
                modelNext = LocalDateTime.parse(next, DATETIME_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new IllegalValueException(APPOINTMENT_START_MESSAGE_CONSTRAINTS);
            }
        }

        Recurrence modelRecurrence = Recurrence.NONE;
        if (recurrence != null) {
            try {
                modelRecurrence = Recurrence.valueOf(recurrence);
            } catch (IllegalArgumentException e) {
                throw new IllegalValueException(
                        "Appointment recurrence must be one of: WEEKLY, BIWEEKLY, MONTHLY, NONE");
            }
        }

        AttendanceRecords modelAttendanceRecords = AttendanceRecords.EMPTY;
        if (attendanceRecords != null) {
            for (JsonAdaptedAppointmentAttendance attendanceRecord : attendanceRecords) {
                modelAttendanceRecords = modelAttendanceRecords.addAttendance(attendanceRecord.toModelType());
            }
        }

        return new Appointment(modelRecurrence, modelStart, modelNext,
                modelAttendanceRecords, description == null ? "" : description);
    }
}
