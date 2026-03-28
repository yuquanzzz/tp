package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.academic.Subject;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.model.session.Appointment;

/**
 * Panel that displays detailed information for the selected person.
 */
public class PersonDetailPanel extends UiPart<Region> {

    private static final String FXML = "PersonDetailPanel.fxml";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d MMM uuuu, h:mma");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM uuuu");

    @FXML
    private VBox contentContainer;

    @FXML
    private Label emptyStateLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label parentNameLabel;

    @FXML
    private Label parentPhoneLabel;

    @FXML
    private Label parentEmailLabel;

    @FXML
    private VBox appointmentListContainer;

    @FXML
    private Label paymentAmountLabel;

    @FXML
    private Label paymentDueDateLabel;

    @FXML
    private FlowPane paymentHistoryFlowPane;

    @FXML
    private FlowPane tagsFlowPane;

    @FXML
    private FlowPane subjectsFlowPane;

    @FXML
    private Label academicsNotesLabel;

    /**
     * Creates a {@code PersonDetailPanel}.
     */
    public PersonDetailPanel() {
        super(FXML);
        showEmptyState();
    }

    /**
     * Updates this panel to display the details of {@code person}, or clears the panel when null.
     */
    public void displayPerson(Person person) {
        if (person == null) {
            showEmptyState();
            return;
        }

        requireNonNull(person);

        nameLabel.setText(person.getName().fullName);
        phoneLabel.setText(person.getPhone().value);
        emailLabel.setText(person.getEmail().value);
        addressLabel.setText(person.getAddress().value);
        parentNameLabel.setText(person.getGuardian()
                .map(g -> g.getName().fullName).orElse("-"));
        parentPhoneLabel.setText(person.getGuardian()
                .flatMap(g -> g.getPhone()).map(p -> p.value).orElse("-"));
        parentEmailLabel.setText(person.getGuardian()
                .flatMap(g -> g.getEmail()).map(e -> e.value).orElse("-"));
        paymentAmountLabel.setText(formatAmount(person.getBilling().getTuitionFee()));
        paymentDueDateLabel.setText(formatDate(person.getBilling().getCurrentDueDate()));

        appointmentListContainer.getChildren().clear();
        tagsFlowPane.getChildren().clear();
        subjectsFlowPane.getChildren().clear();
        paymentHistoryFlowPane.getChildren().clear();

        if (person.getTags().isEmpty()) {
            Label noTagsLabel = new Label("-");
            noTagsLabel.getStyleClass().add("detail-field-value");
            tagsFlowPane.getChildren().add(noTagsLabel);
        } else {
            List<Tag> sortedTags = person.getSortedTags();

            for (int i = 0; i < sortedTags.size(); i++) {
                Tag tag = sortedTags.get(i);

                Label tagLabel = new Label((i + 1) + ". " + tag.tagName);
                tagLabel.getStyleClass().add("detail-tag");
                tagsFlowPane.getChildren().add(tagLabel);
            }
        }

        // Academics
        if (person.getAcademics().getSubjects().isEmpty()) {
            Label noSubjectsLabel = new Label("-");
            noSubjectsLabel.getStyleClass().add("detail-field-value");
            subjectsFlowPane.getChildren().add(noSubjectsLabel);
        } else {
            List<Subject> sortedSubjects = person.getAcademics().getSortedSubjects();

            for (int i = 0; i < sortedSubjects.size(); i++) {
                Subject subject = sortedSubjects.get(i);

                Label subjectLabel = new Label((i + 1) + ". " + subject.toString());
                subjectLabel.getStyleClass().add("detail-subject-tag");
                subjectsFlowPane.getChildren().add(subjectLabel);
            }
        }

        String description = person.getAcademics().getDescription().orElse("");
        academicsNotesLabel.setText(description.isEmpty() ? "-" : description);

        if (person.getAppointments().isEmpty()) {
            Label noAppointmentsLabel = new Label("No appointments");
            noAppointmentsLabel.getStyleClass().add("detail-field-value");
            appointmentListContainer.getChildren().add(noAppointmentsLabel);
        } else {
            for (int index = 0; index < person.getAppointments().size(); index++) {
                appointmentListContainer.getChildren().add(
                        createAppointmentSection(index + 1, person.getAppointments().get(index)));
            }
        }

        // Display payment history
        if (person.getPaymentHistory().getPaidDates().isEmpty()) {
            Label noPaymentsLabel = new Label("No payment history");
            noPaymentsLabel.getStyleClass().add("detail-field-value");
            paymentHistoryFlowPane.getChildren().add(noPaymentsLabel);
        } else {
            person.getPaymentHistory().getPaidDates().stream()
                    .sorted(java.util.Comparator.reverseOrder()) // Most recent first
                    .forEach(date -> {
                        Label paymentLabel = new Label(formatDate(date));
                        paymentLabel.getStyleClass().add("detail-payment-date");
                        paymentHistoryFlowPane.getChildren().add(paymentLabel);
                    });
        }

        contentContainer.setManaged(true);
        contentContainer.setVisible(true);
        emptyStateLabel.setManaged(false);
        emptyStateLabel.setVisible(false);
    }

    private void showEmptyState() {
        appointmentListContainer.getChildren().clear();
        tagsFlowPane.getChildren().clear();
        subjectsFlowPane.getChildren().clear();
        paymentHistoryFlowPane.getChildren().clear();
        contentContainer.setManaged(false);
        contentContainer.setVisible(false);
        emptyStateLabel.setManaged(true);
        emptyStateLabel.setVisible(true);
    }

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return "-";
        }
        return value.format(DATE_TIME_FORMATTER);
    }

    private String formatDate(LocalDate value) {
        if (value == null) {
            return "-";
        }
        return value.format(DATE_FORMATTER);
    }

    private String formatAmount(double amount) {
        return String.format("$%.2f", amount);
    }

    private String formatAttendance(Attendance attendance) {
        String status = attendance.hasAttended() ? "Present" : "Absent";
        return status + ": " + formatDate(attendance.getRecordedDate());
    }

    private String formatAppointment(int appointmentIndex, Appointment appointment) {
        return appointmentIndex + ". " + formatDateTime(appointment.getNext()) + " - " + appointment.getDescription();
    }

    private VBox createAppointmentSection(int appointmentIndex, Appointment appointment) {
        VBox appointmentSection = new VBox(6);

        Label appointmentLabel = new Label(formatAppointment(appointmentIndex, appointment));
        appointmentLabel.getStyleClass().add("detail-section-title");
        appointmentLabel.setWrapText(true);

        Label attendanceTitle = new Label("Attendance");
        attendanceTitle.getStyleClass().add("detail-field-label");

        FlowPane attendancePane = new FlowPane();
        attendancePane.setHgap(6);
        attendancePane.setVgap(6);
        attendancePane.setPrefWrapLength(320);

        if (appointment.getAttendance().isEmpty()) {
            Label noAttendanceLabel = new Label("No attendance history");
            noAttendanceLabel.getStyleClass().add("detail-field-value");
            attendancePane.getChildren().add(noAttendanceLabel);
        } else {
            java.util.List<Attendance> attendanceRecords = appointment.getAttendance().getRecords();
            for (int index = attendanceRecords.size() - 1; index >= 0; index--) {
                Label attendanceLabel = new Label(formatAttendance(attendanceRecords.get(index)));
                attendanceLabel.getStyleClass().add("detail-attendance-date");
                attendancePane.getChildren().add(attendanceLabel);
            }
        }

        appointmentSection.getChildren().addAll(appointmentLabel, attendanceTitle, attendancePane);
        return appointmentSection;
    }
}
