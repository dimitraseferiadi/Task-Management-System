package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import logic.TaskManager;
import model.Task;

import java.time.LocalDate;
import java.util.List;

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
    private ComboBox<String> cmbPriority, cmbStatus;

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

        // Add click listeners
        taskListView.setOnMouseClicked(this::onTaskSelected);
        categoryListView.setOnMouseClicked(this::onCategorySelected);
        priorityListView.setOnMouseClicked(this::onPrioritySelected);

        // Refresh summary labels
        refreshSummary();
    }

    private void refreshLists() {
        taskList.setAll(taskManager.getTasks());
        categoryList.setAll(taskManager.getCategories());
        priorityList.setAll(taskManager.getPriorities());
    }

    private void refreshSummary() {
        lblTotalTasks.setText("Total Tasks: " + taskManager.getTasks().size());
        lblCompletedTasks.setText("Completed Tasks: " + taskManager.getCompletedTaskCount());
        lblDelayedTasks.setText("Delayed Tasks: " + taskManager.getDelayedTaskCount());
        lblTasksDue.setText("Tasks Due in 7 Days: " + taskManager.getTasksDueSoonCount());
    }

    @FXML
    private void onAddTask() {
        String title = txtTaskTitle.getText();
        String description = txtTaskDescription.getText();
        String category = categoryListView.getSelectionModel().getSelectedItem();
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
        selectedTask.setCategory(categoryListView.getSelectionModel().getSelectedItem());
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
            cmbPriority.setValue(selectedTask.getPriority());
            cmbStatus.setValue(selectedTask.getStatus());
            dpTaskDeadline.setValue(selectedTask.getDeadline());
        }
    }

    @FXML
    private void onAddCategory() {
        String categoryName = txtCategoryName.getText();
        if (categoryName.isEmpty()) {
            showAlert("Error", "Category name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.addCategory(categoryName);
        refreshLists();
    }

    @FXML
    private void onDeleteCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert("Error", "Please select a category to delete.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.deleteCategory(selectedCategory);
        refreshLists();
    }

    @FXML
    private void onAddPriority() {
        String priorityName = txtPriorityName.getText();
        if (priorityName.isEmpty()) {
            showAlert("Error", "Priority name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.addPriority(priorityName);
        refreshLists();
    }

    @FXML
    private void onDeletePriority() {
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority == null || selectedPriority.equals("Default")) {
            showAlert("Error", "Cannot delete default priority.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.deletePriority(selectedPriority);
        refreshLists();
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
