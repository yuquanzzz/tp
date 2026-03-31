package seedu.address.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.academic.Subject;
import seedu.address.model.person.Person;
import seedu.address.model.session.Appointment;
import seedu.address.model.tag.Tag;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;
    @FXML
    private FlowPane subjects;
    @FXML
    private VBox appointmentList;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex, boolean showAppointments) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);

        if (showAppointments && !person.getAppointments().isEmpty()) {
            for (int index = 0; index < person.getAppointments().size(); index++) {
                Appointment appointment = person.getAppointments().get(index);
                Label appointmentLabel = new Label(formatAppointment(index + 1, appointment));
                appointmentLabel.getStyleClass().add("cell_small_label");
                appointmentLabel.setWrapText(true);
                appointmentList.getChildren().add(appointmentLabel);
            }
        } else {
            appointmentList.setVisible(false);
            appointmentList.setManaged(false);
        }

        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);

        List<Tag> sortedTags = person.getSortedTags();
        for (int i = 0; i < sortedTags.size(); i++) {
            Tag tag = sortedTags.get(i);
            tags.getChildren().add(new Label((i + 1) + ". " + tag.tagName));
        }

        List<Subject> sortedSubjects = person.getAcademics().getSortedSubjects();

        for (int i = 0; i < sortedSubjects.size(); i++) {
            Subject subject = sortedSubjects.get(i);

            Label subjectLabel = new Label((i + 1) + ". " + subject.toString());
            subjectLabel.getStyleClass().add("tag");
            subjects.getChildren().add(subjectLabel);
        }
    }

    private String formatAppointment(int appointmentIndex, Appointment appointment) {
        String formattedTime = appointment.getNext().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return "Appt " + appointmentIndex + ": " + formattedTime + " - " + appointment.getDescription();
    }
}
