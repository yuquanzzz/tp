package seedu.address.storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.subject.Subject;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";
    private static final String APPOINTMENT_START_MESSAGE_CONSTRAINTS =
            "Appointment start date-time must be in ISO 8601 local format, e.g. 2026-01-13T08:00:00";
    private static final String LAST_ATTENDANCE_MESSAGE_CONSTRAINTS =
            "Last attendance date-time must be in ISO 8601 local format, e.g. 2026-01-29T08:00:00";
    private static final String PAYMENT_DATE_MESSAGE_CONSTRAINTS =
            "Payment date must be in ISO 8601 local date format, e.g. 2026-01-13";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withResolverStyle(ResolverStyle.STRICT);


    private final String id;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String appointmentStart;
    private final String lastAttendance;
    private final String parentName; // optional, may be null
    private final String parentPhone; // optional, may be null
    private final String parentEmail; // optional, may be null
    private final String paymentDate;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedSubject> subjects = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("id") String id,
            @JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("subjects") List<JsonAdaptedSubject> subjects,
            @JsonProperty("parentName") String parentName,
            @JsonProperty("parentPhone") String parentPhone,
            @JsonProperty("parentEmail") String parentEmail,
            @JsonProperty("appointmentStart") String appointmentStart,
            @JsonProperty("paymentDate") String paymentDate,
            @JsonProperty("lastAttendance") String lastAttendance) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.parentEmail = parentEmail;
        this.appointmentStart = appointmentStart;
        this.paymentDate = paymentDate;
        this.lastAttendance = lastAttendance;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (subjects != null) {
            this.subjects.addAll(subjects);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        id = source.getId().toString();
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        appointmentStart = source.getAppointmentStart()
                .map(value -> value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .orElse(null);
        lastAttendance = source.getLastAttendance()
            .map(value -> value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .orElse(null);
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        subjects.addAll(source.getSubjects().stream()
                .map(JsonAdaptedSubject::new)
                .collect(Collectors.toList()));
        parentName = source.getParentName().map(pn -> pn.fullName).orElse(null);
        parentPhone = source.getParentPhone().map(pp -> pp.value).orElse(null);
        parentEmail = source.getParentEmail().map(pe -> pe.value).orElse(null);
        paymentDate = source.getPaymentDate().map(value -> value.format(DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null);
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final UUID modelId = validateId();
        final Name modelName = validateName(name, "Person's");
        final Phone modelPhone = validatePhone(phone, "Person's");
        final Email modelEmail = validateEmail(email, "Person's");
        final Address modelAddress = validateAddress();
        final Set<Tag> modelTags = validateTags();
        final Set<Subject> modelSubjects = validateSubjects();
        final Name modelParentName = parentName != null ? validateName(parentName, "Parent's") : null;
        final Phone modelParentPhone = parentPhone != null ? validatePhone(parentPhone, "Parent's") : null;
        final Email modelParentEmail = parentEmail != null ? validateEmail(parentEmail, "Parent's") : null;
        final LocalDateTime modelAppointmentStart = validateDateTime(
                appointmentStart, APPOINTMENT_START_MESSAGE_CONSTRAINTS);
        final LocalDate modelPaymentDate = validateDate(paymentDate, PAYMENT_DATE_MESSAGE_CONSTRAINTS);
        final LocalDateTime modelLastAttendance = validateDateTime(
                lastAttendance, LAST_ATTENDANCE_MESSAGE_CONSTRAINTS);

        return new Person(
                modelId, modelName, modelPhone, modelEmail, modelAddress,
                modelTags, modelSubjects,
                Optional.ofNullable(modelParentName),
                Optional.ofNullable(modelParentPhone),
                Optional.ofNullable(modelParentEmail),
                Optional.ofNullable(modelAppointmentStart),
                Optional.ofNullable(modelPaymentDate),
                Optional.ofNullable(modelLastAttendance));
    }

    // ==================== Validation helpers ====================

    private UUID validateId() throws IllegalValueException {
        if (id == null) {
            // Backward compatibility: generate a new UUID for old data files
            return UUID.randomUUID();
        }
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException("Person's ID must be a valid UUID.");
        }
    }

    private Name validateName(String value, String ownerPrefix) throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(value)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(value);
    }

    private Phone validatePhone(String value, String ownerPrefix) throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(value)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(value);
    }

    private Email validateEmail(String value, String ownerPrefix) throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(value)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(value);
    }

    private Address validateAddress() throws IllegalValueException {
        if (address == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(address);
    }

    private Set<Tag> validateTags() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        if (tags != null) {
            for (JsonAdaptedTag tag : tags) {
                personTags.add(tag.toModelType());
            }
        }
        return new HashSet<>(personTags);
    }

    private Set<Subject> validateSubjects() throws IllegalValueException {
        final List<Subject> personSubjects = new ArrayList<>();
        if (subjects != null) {
            for (JsonAdaptedSubject subject : subjects) {
                personSubjects.add(subject.toModelType());
            }
        }
        return new HashSet<>(personSubjects);
    }

    private LocalDateTime validateDateTime(String value, String errorMessage) throws IllegalValueException {
        if (value == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalValueException(errorMessage);
        }
    }

    private LocalDate validateDate(String value, String errorMessage) throws IllegalValueException {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalValueException(errorMessage);
        }
    }
}
