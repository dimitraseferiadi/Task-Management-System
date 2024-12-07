package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.TaskManager;
import model.Task;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MainController {
	
    @FXML
    private Label lblTotalTasks, lblCompletedTasks, lblDelayedTasks, lblTasksDue;

    @FXML
    private ListView<Task> taskListView;

    @FXML
    private ListView<String> categoryListView;

    @FXML
    private ListView<String> priorityListView;

    @FXML
    private VBox taskDetailsBox;

    @FXML
    private TextField txtTaskTitle, txtCategoryName, txtPriorityName, txtSearch;

    @FXML
    private DatePicker dpTaskDeadline;

    @FXML
    private TextArea txtTaskDescription;

    @FXML
    private ComboBox<String> cmbPriority, cmbStatus, cmbCategory;

    private TaskManager taskManager;

    private ObservableList<Task> taskList;
    private ObservableList<String> categoryList;
    private ObservableList<String> priorityList;

    public void initialize() {
        taskManager = new TaskManager();
        
        taskList = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList();
        priorityList = FXCollections.observableArrayList();

        // Load data
        refreshLists();

        // Bind lists to ListViews
        taskListView.setItems(taskList);
        categoryListView.setItems(categoryList);
        priorityListView.setItems(priorityList);

        // Configure ComboBoxes
        cmbPriority.setItems(priorityList);
        cmbStatus.setItems(FXCollections.observableArrayList("Open", "In Progress", "Postponed", "Completed", "Delayed"));
        cmbCategory.setItems(categoryList);

        // Add click listeners
        taskListView.setOnMouseClicked(this::onTaskSelected);
        categoryListView.setOnMouseClicked(this::onCategorySelected);
        priorityListView.setOnMouseClicked(this::onPrioritySelected);

        // Refresh summary labels
        refreshSummary();
    }
    
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;

        // Initialize observable lists and refresh the UI
        taskList.setAll(taskManager.getTasks());
        categoryList.setAll(taskManager.getCategories());
        priorityList.setAll(taskManager.getPriorities());
        refreshSummary();
    }
    
    public void refreshPriorities() {
        priorityList.setAll(taskManager.getPriorities());
    }
    
    public void refreshCategories() {
        categoryList.setAll(taskManager.getCategories());
    }


    public void refreshLists() {
        taskList.setAll(taskManager.getTasks());
        categoryList.setAll(taskManager.getCategories());
        priorityList.setAll(taskManager.getPriorities());
        refreshPriorities();
        refreshCategories();
    }

    private void refreshSummary() {
        lblTotalTasks.setText("Total Tasks: " + taskManager.getTasks().size());
        lblCompletedTasks.setText("Completed Tasks: " + taskManager.getCompletedTaskCount());
        lblDelayedTasks.setText("Delayed Tasks: " + taskManager.getDelayedTaskCount());
        lblTasksDue.setText("Tasks Due in 7 Days: " + taskManager.getTasksDueSoonCount());
    }
    
    @FXML
    private void onManagePriorities() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PrioritiesView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Manage Priorities");
            stage.setScene(new Scene(loader.load()));

            // Pass TaskManager instance to the new controller
            PrioritiesController controller = loader.getController();
            controller.initializeWithTaskManager(taskManager, this);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open priorities management window.", Alert.AlertType.ERROR);
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert("Error", "FXML file not found. Check the file path.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onManageCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CategoriesView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Manage Categories");
            stage.setScene(new Scene(loader.load()));

            // Pass TaskManager instance to the new controller
            CategoriesController controller = loader.getController();
            controller.initializeWithTaskManager(taskManager, this);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open categories management window.", Alert.AlertType.ERROR);
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert("Error", "FXML file not found. Check the file path.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void onAddTask() {
        String title = txtTaskTitle.getText();
        String description = txtTaskDescription.getText();
        String category = cmbCategory.getValue();
        String priority = cmbPriority.getValue();
        LocalDate deadline = dpTaskDeadline.getValue();
        String status = cmbStatus.getValue();

        if (title.isEmpty() || category == null || priority == null || deadline == null || status == null) {
            showAlert("Error", "Please fill in all task fields.", Alert.AlertType.ERROR);
            return;
        }

        Task newTask = new Task(title, description, category, priority, deadline, status);
        taskManager.addTask(newTask);
        refreshLists();
        refreshSummary();
    }

    @FXML
    private void onEditTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to edit.", Alert.AlertType.ERROR);
            return;
        }

        selectedTask.setTitle(txtTaskTitle.getText());
        selectedTask.setDescription(txtTaskDescription.getText());
        selectedTask.setCategory(cmbCategory.getValue());
        selectedTask.setPriority(cmbPriority.getValue());
        selectedTask.setDeadline(dpTaskDeadline.getValue());
        selectedTask.setStatus(cmbStatus.getValue());

        taskManager.updateTask(selectedTask);
        refreshLists();
        refreshSummary();
    }

    @FXML
    private void onDeleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to delete.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.deleteTask(selectedTask);
        refreshLists();
        refreshSummary();
    }

    private void onTaskSelected(MouseEvent event) {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            txtTaskTitle.setText(selectedTask.getTitle());
            txtTaskDescription.setText(selectedTask.getDescription());
            cmbCategory.setValue(selectedTask.getCategory());
            cmbPriority.setValue(selectedTask.getPriority());
            cmbStatus.setValue(selectedTask.getStatus());
            dpTaskDeadline.setValue(selectedTask.getDeadline());
        }
    }
    
    private void refreshTaskDetails() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            // Check if the current task's category or priority still exists
            if (!categoryList.contains(selectedTask.getCategory()) || !priorityList.contains(selectedTask.getPriority())) {
                // If category or priority no longer exists, clear task details
                clearTaskDetails();
            } else {
                // Update task details fields
                txtTaskTitle.setText(selectedTask.getTitle());
                txtTaskDescription.setText(selectedTask.getDescription());
                cmbCategory.setValue(selectedTask.getCategory());
                cmbPriority.setValue(selectedTask.getPriority());
                cmbStatus.setValue(selectedTask.getStatus());
                dpTaskDeadline.setValue(selectedTask.getDeadline());
            }
        }
    }
    
    private void clearTaskDetails() {
        txtTaskTitle.clear();
        txtTaskDescription.clear();
        cmbCategory.setValue(null);
        cmbPriority.setValue(null);
        cmbStatus.setValue(null);
        dpTaskDeadline.setValue(null);
    }
    
    public void onCategoryModified() {
        refreshTaskDetails();
    }

    public void onPriorityModified() {
        refreshTaskDetails();
    }

    @FXML
    private void onSearchTask() {
        String query = txtSearch.getText();
        List<Task> results = taskManager.searchTasks(query);
        taskList.setAll(results);
    }

    private void onCategorySelected(MouseEvent event) {
        // Implement category-specific actions if needed
    }

    private void onPrioritySelected(MouseEvent event) {
        // Implement priority-specific actions if needed
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
