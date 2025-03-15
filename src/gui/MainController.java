package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import logic.TaskManager;
import model.Reminder;

import model.Task;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public class MainController {
	
    @FXML
    private Label lblTotalTasks, lblCompletedTasks, lblDelayedTasks, lblTasksDue;

    @FXML
    private ListView<String> categoryListView;

    @FXML
    private ListView<String> priorityListView;
    
    @FXML
    private ListView<String> reminderListView;

    @FXML
    private VBox taskDetailsBox;

    @FXML
    private TextField txtTaskTitle, txtCategoryName, txtPriorityName, txtSearch;

    @FXML
    private DatePicker dpTaskDeadline;

    @FXML
    private TextArea txtTaskDescription;

    @FXML
    private ComboBox<String> cmbPriority, cmbStatus, cmbCategory, cmbCategoryFilter;
    
    @FXML
    private RadioButton rbTitle, rbCategory, rbPriority;

    @FXML
    private ToggleGroup searchToggleGroup;

    @FXML
    private FlowPane taskDisplayArea;
    
    // Task Statistics Overview
    @FXML
    private Pane paneTotalTasks;
    
    @FXML
    private Pane paneCompletedTasks;
    
    @FXML
    private Pane paneDelayedTasks;
    
    @FXML
    private Pane paneTasksDue;
    
    @FXML
    private ListView<String> lstReminders;
    
    private TaskManager taskManager;

    private ObservableList<Task> taskList;
    private ObservableList<String> categoryList;
    private ObservableList<String> priorityList;
    
    private ObservableList<String> reminderDisplayList;

    /**
     * Initializes the main controller by setting up the task manager, observable lists,
     * binding UI elements, updating filters, and refreshing the task display and statistics.
     * <p>
     * This method is automatically called after the FXML file is loaded.
     * </p>
     */
    public void initialize() {
        taskManager = new TaskManager();
        
        taskList = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList();
        priorityList = FXCollections.observableArrayList();
        reminderDisplayList = FXCollections.observableArrayList();
        
        // Load data
        refreshLists();

        // Bind lists to ListViews
        categoryListView.setItems(categoryList);
        priorityListView.setItems(priorityList);
        lstReminders.setItems(reminderDisplayList);
        
        updateCategoryFilter();
 
        searchToggleGroup = new ToggleGroup();
        rbTitle.setToggleGroup(searchToggleGroup);
        rbCategory.setToggleGroup(searchToggleGroup);
        rbPriority.setToggleGroup(searchToggleGroup);
        
        // Refresh summary labels  
        updateOverdueTasks();
        
        refreshSummary();
        refreshTaskDisplay(taskManager.getTasks());
        refreshReminders();
        
        // Check for overdue tasks and display popup
        showOverdueTasksPopup();
    }
    
    /**
     * Sets the TaskManager instance for the controller and updates the UI to reflect the current tasks,
     * categories, and priorities.
     *
     * @param taskManager the TaskManager instance used to retrieve and manage tasks
     */
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;

        // Initialize observable lists and refresh the UI
        taskList.setAll(taskManager.getTasks());
        categoryList.setAll(taskManager.getCategories());
        priorityList.setAll(taskManager.getPriorities());
        updateOverdueTasks();
        refreshSummary();
    }

    /**
     * Refreshes the lists of categories and priorities from the TaskManager,
     * and updates the task display and summary information.
     */
    public void refreshLists() {
        categoryList.setAll(taskManager.getCategories());
        priorityList.setAll(taskManager.getPriorities());
        refreshSummary();
        refreshTasks();
    }
    
    /**
     * Refreshes the task list by updating the internal list of tasks, refreshing the displayed tasks,
     * updating the category filter, and refreshing the summary and reminders.
     */
    public void refreshTasks() {
    	taskList.setAll(taskManager.getTasks());
    	refreshTaskDisplay(taskManager.getTasks());
    	
    	updateCategoryFilter();
    	
    	refreshSummary();
    	refreshReminders();
    }

    private void refreshSummary() {
        lblTotalTasks.setText("Total Tasks: " + taskManager.getTasks().size());
        lblCompletedTasks.setText("Completed Tasks: " + taskManager.getCompletedTaskCount());
        lblDelayedTasks.setText("Delayed Tasks: " + taskManager.getDelayedTaskCount());
        lblTasksDue.setText("Tasks Due in 7 Days: " + taskManager.getTasksDueSoonCount());
    }
    
    private void refreshReminders() {
        reminderDisplayList.clear();
        for (Task task : taskManager.getTasks()) {
            for (Reminder reminder : task.getReminders()) {
                String reminderText = "Task: " + task.getTitle() + " | Reminder: " + reminder.getReminderDate();
                reminderDisplayList.add(reminderText);
            }
        }
    }

    
    private void refreshTaskDisplay(List<Task> tasks) {
        taskDisplayArea.getChildren().clear();
        for (Task task : tasks) {
            VBox taskCard = createTaskCard(task);
            taskDisplayArea.getChildren().add(taskCard);
        }
    }
    
    private VBox createTaskCard(Task task) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #f4e6f9; -fx-border-color: #b185db; -fx-border-radius: 10; -fx-padding: 10;");
        card.setPrefWidth(180);

        Label title = new Label(task.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #4b2d57;");

        Label details = new Label("\nDescription: " + task.getDescription() + "\nCategory: " + task.getCategory() + "\nPriority: " + task.getPriority() + "\nDue: " + task.getDeadline() + "\nStatus: " + task.getStatus());
        details.setWrapText(true);
        details.setStyle("-fx-text-fill: #985f99;");

        card.getChildren().addAll(title, details);

        // On hover effects with lighter tones
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #d4a5e6; -fx-border-color: #b185db; -fx-border-radius: 10; -fx-padding: 10;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #f4e6f9; -fx-border-color: #b185db; -fx-border-radius: 10; -fx-padding: 10;"));
        
        card.setOnMouseClicked(e -> openTaskWindow(task));
        
        return card;
    }
    
    private void updateOverdueTasks() {
        LocalDate today = LocalDate.now();
        for (Task task : taskManager.getTasks()) {
            if (!task.getStatus().equals("Completed") && task.getDeadline().isBefore(today)) {
                task.setStatus("Delayed");
            }
        }
    }
    
    private void updateCategoryFilter() {
        ObservableList<String> categoryFilterList = FXCollections.observableArrayList();
        categoryFilterList.add("All Categories"); 
        categoryFilterList.addAll(categoryList);

        cmbCategoryFilter.setItems(categoryFilterList);
        cmbCategoryFilter.getSelectionModel().select("All Categories");
    }

    
    @FXML
    private void onManageTasks() {
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

    
    private void openTaskWindow(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Edit Task");
            stage.setScene(new Scene(loader.load()));

            // Pass task and task manager to the new controller
            TaskController controller = loader.getController();
            controller.initializeWithTask(task, taskManager, this);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open the task editing window.", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Opens the Add Task window where the user can create a new task.
     * <p>
     * This method loads the corresponding FXML view and initializes the TaskController
     * for adding a new task. The window is displayed to the user without mentioning the internal exceptions.
     * </p>
     */
    @FXML
    public void openAddTaskWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddTaskView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add Task");
            stage.setScene(new Scene(loader.load()));

            // Pass task and task manager to the new controller
            TaskController controller = loader.getController();
            controller.initializeForNewTask(taskManager, this);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open the task adding window.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void onSearch() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            refreshTaskDisplay(taskManager.getTasks());
            return;
        }

        RadioButton selectedOption = (RadioButton) searchToggleGroup.getSelectedToggle();
        List<Task> filteredTasks;

        if (selectedOption == rbTitle) {
            filteredTasks = taskManager.searchTasksByTitle(query);
        } else if (selectedOption == rbCategory) {
            filteredTasks = taskManager.searchTasksByCategory(query);
        } else {
            filteredTasks = taskManager.searchTasksByPriority(query);
        }

        refreshTaskDisplay(filteredTasks);
    }
    
    @FXML
    private void onStatisticBoxClicked(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        List<Task> filteredTasks;

        if (clickedLabel == lblTotalTasks) {
            filteredTasks = taskManager.getTasks();
        } else if (clickedLabel == lblCompletedTasks) {
            filteredTasks = taskManager.getCompletedTasks();
        } else if (clickedLabel == lblDelayedTasks) {
            filteredTasks = taskManager.getDelayedTasks();
        } else {
            filteredTasks = taskManager.getTasksDueSoon();
        }

        refreshTaskDisplay(filteredTasks);
    }
    
    private void showOverdueTasksPopup() {
        long overdueCount = taskManager.getTasks().stream()
                .filter(task -> "Delayed".equals(task.getStatus()))
                .count();

        if (overdueCount > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Overdue Tasks");
            alert.setHeaderText("You have overdue tasks!");
            alert.setContentText("There are " + overdueCount + " tasks that are in a 'Delayed' state.");

            alert.showAndWait();
        }
    }
    
    @FXML
    private void onCategoryFilterSelected() {
        String selectedCategory = cmbCategoryFilter.getSelectionModel().getSelectedItem();
        if (selectedCategory == null || selectedCategory.equals("All Categories")) {
            // Show all tasks if "All Categories" is selected
            refreshTaskDisplay(taskManager.getTasks());
        } else {
            // Filter tasks by the selected category
            List<Task> filteredTasks = taskManager.getTasksByCategory(selectedCategory);
            refreshTaskDisplay(filteredTasks);
        }
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
