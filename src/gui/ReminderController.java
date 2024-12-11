package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Reminder;
import model.Task;

import java.time.LocalDate;

public class ReminderController {

    @FXML
    private ListView<Reminder> lstReminders;

    @FXML
    private ComboBox<String> cmbReminderType;

    @FXML
    private DatePicker dpCustomDate;

    private Task task;
    private TaskController taskController;

    // List of reminder types
    private ObservableList<String> reminderTypes = FXCollections.observableArrayList(
            "One Day Before", "One Week Before", "One Month Before", "Custom"
    );

    // Initialize method called automatically when the FXML is loaded
    public void initialize() {
        cmbReminderType.setItems(reminderTypes);
        
        cmbReminderType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Custom".equals(newValue)) {
                dpCustomDate.setVisible(true);
            } else {
                dpCustomDate.setVisible(false);
                dpCustomDate.setValue(null); // Clear the DatePicker value when hidden
            }
        });
    }

    // Method to initialize the controller with task data
    public void initializeWithTask(Task task, TaskController taskController) {
        this.task = task;
        this.taskController = taskController;
        lstReminders.setItems(task.getReminders()); // Bind the ListView to the task's reminders
    }

    // Add a new reminder based on the selected type
    @FXML
    private void onAddReminder() {
        String selectedType = cmbReminderType.getValue();
        LocalDate reminderDate = null;

        if (selectedType == null) {
            showAlert("Please select a reminder type.");
            return;
        }

        switch (selectedType) {
            case "One Day Before":
                reminderDate = task.getDeadline().minusDays(1);
                break;
            case "One Week Before":
                reminderDate = task.getDeadline().minusWeeks(1);
                break;
            case "One Month Before":
                reminderDate = task.getDeadline().minusMonths(1);
                break;
            case "Custom":
                reminderDate = dpCustomDate.getValue();
                if (reminderDate == null) {
                    showAlert("Please select a custom date.");
                    return;
                }
                break;
        }
        
        if (isReminderAlreadyExists(reminderDate)) {
            showAlert("A reminder already exists for this date.");
            return;
        }

        if (reminderDate.isAfter(task.getDeadline())) {
            showAlert("Reminder date cannot be after the task deadline.");
            return;
        }

        Reminder newReminder = new Reminder(selectedType, reminderDate);
        task.getReminders().add(newReminder);
        lstReminders.refresh(); // Refresh the ListView to show the new reminder
    }

    // Edit the selected reminder
    @FXML
    private void onEditReminder() {
        Reminder selectedReminder = lstReminders.getSelectionModel().getSelectedItem();

        if (selectedReminder == null) {
            showAlert("Please select a reminder to edit.");
            return;
        }

        String selectedType = cmbReminderType.getValue();
        LocalDate reminderDate = null;

        if (selectedType == null) {
            showAlert("Please select a reminder type.");
            return;
        }

        switch (selectedType) {
            case "One Day Before":
                reminderDate = task.getDeadline().minusDays(1);
                break;
            case "One Week Before":
                reminderDate = task.getDeadline().minusWeeks(1);
                break;
            case "One Month Before":
                reminderDate = task.getDeadline().minusMonths(1);
                break;
            case "Custom":
                reminderDate = dpCustomDate.getValue();
                if (reminderDate == null) {
                    showAlert("Please select a custom date.");
                    return;
                }
                break;
        }
        
        if (isReminderAlreadyExists(reminderDate, selectedReminder)) {
            showAlert("A reminder already exists for this date.");
            return;
        }

        if (reminderDate.isAfter(task.getDeadline())) {
            showAlert("Reminder date cannot be after the task deadline.");
            return;
        }

        // Update the selected reminder
        selectedReminder.setType(selectedType);
        selectedReminder.setReminderDate(reminderDate);
        lstReminders.refresh(); // Refresh the ListView to show the updated reminder
    }

    // Delete the selected reminder
    @FXML
    private void onDeleteReminder() {
        Reminder selectedReminder = lstReminders.getSelectionModel().getSelectedItem();

        if (selectedReminder == null) {
            showAlert("Please select a reminder to delete.");
            return;
        }

        task.getReminders().remove(selectedReminder);
        lstReminders.refresh(); // Refresh the ListView to reflect the deletion
    }

    // Close the reminders window
    @FXML
    private void onClose() {
        taskController.loadReminders(); // Refresh reminders in the main task view
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) lstReminders.getScene().getWindow();
        stage.close();
    }

    // Utility method to show alerts
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Check if a reminder already exists for the given date
    private boolean isReminderAlreadyExists(LocalDate reminderDate) {
        for (Reminder reminder : task.getReminders()) {
            if (reminder.getReminderDate().equals(reminderDate)) {
                return true;
            }
        }
        return false;
    }

    // Check if a reminder already exists for the given date (excluding the provided reminder)
    private boolean isReminderAlreadyExists(LocalDate reminderDate, Reminder excludeReminder) {
        for (Reminder reminder : task.getReminders()) {
            if (reminder != excludeReminder && reminder.getReminderDate().equals(reminderDate)) {
                return true;
            }
        }
        return false;
    }
}
