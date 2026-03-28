package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.academic.Academics;
import seedu.address.model.attendance.AttendanceRecords;
import seedu.address.model.billing.Billing;
import seedu.address.model.billing.PaymentHistory;
import seedu.address.model.session.Appointment;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final Address address;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final Academics academics;
    private final List<Appointment> appointments;
    private final Optional<Guardian> guardian;
    private final Billing billing;

    /**
     * Creates a {@code Person} with the given core fields and tags.
     * Fields other than personal details (name, phone, email, and address)
     * are optional and can be empty.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);

        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);

        this.academics = new Academics();

        this.appointments = List.of();
        this.guardian = Optional.empty();
        this.billing = Billing.defaultBilling();
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address,
                  Set<Tag> tags, Academics academics,
                  Optional<Guardian> guardian,
                  List<Appointment> appointments,
                  Billing billing) {

        requireAllNonNull(name, phone, email, address, tags, academics,
                guardian, appointments, billing);

        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);

        this.academics = academics;

        this.guardian = guardian;
        this.appointments = copyAndSortAppointments(appointments);
        this.billing = billing;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public Optional<Appointment> getAppointment() {
        if (appointments.isEmpty()) {
            return Optional.empty();
        }

        LocalDateTime now = LocalDateTime.now();
        return appointments.stream()
                .filter(appointment -> !appointment.getNext().isBefore(now))
                .findFirst()
                .or(() -> Optional.of(appointments.get(appointments.size() - 1)));
    }

    public List<Appointment> getAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    /**
     * Returns the appointment start, or empty if no appointment exists.
     */
    public Optional<LocalDateTime> getAppointmentStart() {
        return getAppointment().map(Appointment::getStart);
    }

    /**
     * Returns the next appointment occurrence, or empty if no appointment exists.
     */
    public Optional<LocalDateTime> getAppointmentNext() {
        return getAppointment().map(Appointment::getNext);
    }

    public Optional<String> getAppointmentDescription() {
        return getAppointment().map(Appointment::getDescription);
    }

    public Billing getBilling() {
        return billing;
    }

    public PaymentHistory getPaymentHistory() {
        return billing.getPaymentHistory();
    }

    public AttendanceRecords getAttendance() {
        return getAppointment().map(Appointment::getAttendance).orElse(AttendanceRecords.EMPTY);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public List<Tag> getSortedTags() {
        return tags.stream()
                .sorted(Comparator.comparing(tag -> tag.tagName.toLowerCase()))
                .toList();
    }

    public Academics getAcademics() {
        return academics;
    }

    /**
     * Returns the guardian wrapped in an Optional, or empty if not set.
     */
    public Optional<Guardian> getGuardian() {
        return guardian;
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null && otherPerson.getName().equals(getName());
    }

    /**
     * Returns an immutable {@code Billing} object with updated payment history
     * and advanced billing cycle if paymentDate is before or on today's date in SG timezone
     * @param paymentDate A valid {@code LocalDate}
     * @return {@code Billing} object
     */
    public Billing recordFeesPaidAndAdvanceBilling(LocalDate paymentDate) {
        Billing updatedBilling = billing.recordTuitionPaid(paymentDate);
        return updatedBilling.advanceDueDate();
    }

    /**
     * Returns an immutable {@code Billing} object with updated payment history after deleting
     * a recorded payment date. Due date is rolled back one recurrence cycle only when deleting
     * the latest chronological payment date.
     * @param paymentDate A valid {@code LocalDate}
     * @return updated {@code Billing} object
     * @throws IllegalArgumentException if paymentDate is not recorded
     */
    public Billing deleteRecordedPayment(LocalDate paymentDate) {
        return billing.deleteRecordedPayment(paymentDate);
    }

    /**
     * Returns a new {@code Billing} instance with an updated tuition fee for this person.
     * @param tuitionFee the new tuition fee amount to apply; must be non-negative
     * @return a new {@code Billing} object with the updated tuition fee and all other billing fields unchanged
     * @throws IllegalArgumentException if {@code tuitionFee} is negative
     */
    public Billing updateTuitionRate(Double tuitionFee) {
        return billing.updateRate(tuitionFee);
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && academics.equals(otherPerson.academics)
                && guardian.equals(otherPerson.guardian)
                && appointments.equals(otherPerson.appointments)
                && billing.equals(otherPerson.billing);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, academics,
                guardian, appointments, billing);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("academics", academics)
                .add("guardian", guardian.orElse(null))
                .add("appointments", appointments)
                .add("billing", billing)
                .toString();
    }

    private List<Appointment> copyAndSortAppointments(List<Appointment> appointments) {
        List<Appointment> copiedAppointments = new ArrayList<>(appointments);
        copiedAppointments.sort(Comparator.comparing(Appointment::getNext)
                .thenComparing(Appointment::getStart)
                .thenComparing(Appointment::getDescription));
        return List.copyOf(copiedAppointments);
    }

}
