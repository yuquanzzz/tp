package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_JC;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().withTags("friend").build();
        assertThrows(UnsupportedOperationException.class, () ->
                person.getTags().stream().findAny().ifPresent(person.getTags()::remove));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same UUID, different data fields -> returns true (copy preserves UUID)
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_JC).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same UUID, different name -> still same person (UUID is identity)
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different UUID (freshly built person) -> returns false
        Person differentPerson = new PersonBuilder().withName("Alice Pauline").build();
        assertFalse(ALICE.isSamePerson(differentPerson));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_JC).build();
        assertFalse(ALICE.equals(editedAlice));

        // different appointment start -> returns false
        editedAlice = new PersonBuilder(ALICE).withAppointmentStart("2026-01-13T08:00:00").build();
        assertFalse(ALICE.equals(editedAlice));

        // different payment date -> returns false
        editedAlice = new PersonBuilder(ALICE).withPaymentDate("2026-01-13").build();
        assertFalse(ALICE.equals(editedAlice));

        // different last attendance -> returns false
        editedAlice = new PersonBuilder(ALICE).withLastAttendance("2026-01-29T08:00:00").build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName()
                + "{id=" + ALICE.getId()
                + ", name=" + ALICE.getName()
                + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail()
                + ", address=" + ALICE.getAddress()
                + ", tags=" + ALICE.getTags()
                + ", subjects=" + ALICE.getSubjects()
                + ", parentName=" + ALICE.getParentName().orElse(null)
                + ", parentPhone=" + ALICE.getParentPhone().orElse(null)
                + ", parentEmail=" + ALICE.getParentEmail().orElse(null)
                + ", appointmentStart=" + ALICE.getAppointmentStart()
                + ", paymentDate=" + ALICE.getPaymentDate()
                + ", lastAttendance=" + ALICE.getLastAttendance()
                + "}";

        assertEquals(expected, ALICE.toString());
    }
}
