package gui;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

    private Task task;
    private TaskManager taskManager;
    private MainController mainController;
    
    private boolean isNewTask = false;
    
    // private ObservableList<Task> taskList;
    private ObservableList<String> categoryList;
    private ObservableList<String> priorityList;
    
    
    public void initialize() {
        categoryList = FXCollections.observableArrayList();
        priorityList = FXCollections.observableArrayList();
        
        cmbPriority.setItems(priorityList);
        cmbStatus.setItems(FXCollections.observableArrayList("Open", "In Progress", "Postponed", "Completed", "Delayed"));
        cmbCategory.setItems(categoryList);
    }
    
    public void initializeForNewTask(TaskManager taskManager, MainController mainController) {
        this.taskManager = taskManager;
        this.mainController = mainController;
        this.isNewTask = true;

        categoryList.setAll(taskManager.getCategories());
        priorityList.setAll(taskManager.getPriorities());

        cmbStatus.setValue("Open"); // Default status for new tasks
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
            taskManager.addTask(task);
        } else {
            task.setTitle(txtTitle.getText());
            task.setDescription(txtDescription.getText());
            task.setCategory(cmbCategory.getValue());
            task.setPriority(cmbPriority.getValue());
            task.setDeadline(dpDeadline.getValue());
            task.setStatus(cmbStatus.getValue());

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
