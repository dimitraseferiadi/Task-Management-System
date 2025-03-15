package gui;

import java.io.IOException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Reminder;
import model.Task;
import logic.TaskManager;

public class TaskController {

    @FXML
    private TextField txtTitle;

    @FXML
    private TextArea txtDescription;

    @FXML
    private ComboBox<String> cmbCategory;

    @FXML
    private ComboBox<String> cmbPriority;

    @FXML
    private DatePicker dpDeadline;

    @FXML
    private ComboBox<String> cmbStatus;
    
    @FXML
    private VBox reminderSection;
    
    @FXML
    private ListView<String> lstReminders;
    
    private Task task;
    private TaskManager taskManager;
    private MainController mainController;
    
    private boolean isNewTask = false;

    private ObservableList<String> categoryList;
    private ObservableList<String> priorityList;
    
    
    public void initialize() {
        categoryList = FXCollections.observableArrayList();
        priorityList = FXCollections.observableArrayList();
        
        cmbPriority.setItems(priorityList);
        cmbStatus.setItems(FXCollections.observableArrayList("Open", "In Progress", "Postponed", "Completed", "Delayed"));
        cmbCategory.setItems(categoryList);
        
        dpDeadline.valueProperty().addListener((observable, oldDate, newDate) -> {
            if (newDate != null && task != null) {
                updateAutomaticReminders(newDate);
            }
        });
    }
    
    public void initializeForNewTask(TaskManager taskManager, MainController mainController) {
        this.taskManager = taskManager;
        this.mainController = mainController;
        this.isNewTask = true;

        categoryList.setAll(taskManager.getCategories());
        priorityList.setAll(taskManager.getPriorities());

        cmbStatus.setValue("Open"); // Default status for new tasks
        cmbPriority.setValue("Default"); // Default priority for new tasks
    }

    // Method to initialize the controller with task data
    public void initializeWithTask(Task task, TaskManager taskManager, MainController mainController) {
        this.task = task;
        this.taskManager = taskManager;
        this.mainController = mainController;
        
        categoryList.setAll(taskManager.getCategories());
        priorityList.setAll(taskManager.getPriorities());

        txtTitle.setText(task.getTitle());
        txtDescription.setText(task.getDescription());
        cmbCategory.setValue(task.getCategory());
        cmbPriority.setValue(task.getPriority());
        dpDeadline.setValue(task.getDeadline());
        cmbStatus.setValue(task.getStatus());
        
        if ("Completed".equals(task.getStatus())) {
        	reminderSection.setVisible(false);
        } else {
            loadReminders(); // Otherwise, load the reminders as usual
        }
    }

    @FXML
    private void onSave() {
        if (isNewTask) {
        	 String title = txtTitle.getText();
             String description = txtDescription.getText();
             String category = cmbCategory.getValue();
             String priority = cmbPriority.getValue();
             LocalDate deadline = dpDeadline.getValue();
             String status = cmbStatus.getValue();

             if (title.isEmpty() || category == null || priority == null || deadline == null || status == null) {
                 showAlert("Error", "Please fill in all task fields.", Alert.AlertType.ERROR);
                 return;
             }

            task = new Task(title, description, category, priority, deadline, status);
            task.clearRemindersIfCompleted();
            taskManager.addTask(task);
        } else {
            task.setTitle(txtTitle.getText());
            task.setDescription(txtDescription.getText());
            task.setCategory(cmbCategory.getValue());
            task.setPriority(cmbPriority.getValue());
            task.setDeadline(dpDeadline.getValue());
            task.setStatus(cmbStatus.getValue());
            
            task.clearRemindersIfCompleted();
            taskManager.updateTask(task);
        }

        mainController.refreshTasks();
        closeWindow();
    }

    @FXML
    private void onDelete() {
        taskManager.deleteTask(task);
        mainController.refreshLists();

        closeWindow();
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }
    
    public void loadReminders() {
        lstReminders.getItems().clear();
        if (!"Completed".equals(task.getStatus())) {
	        for (Reminder reminder : task.getReminders()) {
	            lstReminders.getItems().add(reminder.toString());
	        }
        }
    }
    
    @FXML
    private void onManageReminders() {
        // Open a new window to manage reminders
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReminderView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Manage Reminders");
            stage.setScene(new Scene(loader.load()));

            ReminderController reminderController = loader.getController();
            reminderController.initializeWithTask(task, this);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void updateAutomaticReminders(LocalDate newDeadline) {
        // Iterate over task reminders
        for (Reminder reminder : task.getReminders()) {
            // Only update reminders that are not custom
            if (!"Custom".equals(reminder.getType())) {
                LocalDate updatedDate = null;
                switch (reminder.getType()) {
                    case "One Day Before":
                        updatedDate = newDeadline.minusDays(1);
                        break;
                    case "One Week Before":
                        updatedDate = newDeadline.minusWeeks(1);
                        break;
                    case "One Month Before":
                        updatedDate = newDeadline.minusMonths(1);
                        break;
                }
                // Update the reminder if a new date was computed
                if (updatedDate != null) {
                    reminder.setReminderDate(updatedDate);
                }
            }
        }
        // Optionally, if you have a ListView displaying reminders, refresh it here
        if (lstReminders != null) {
            lstReminders.refresh();
        }
    }


    private void closeWindow() {
        Stage stage = (Stage) txtTitle.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
