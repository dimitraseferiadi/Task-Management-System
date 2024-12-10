package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.TaskManager;

import java.util.Optional;


public class PrioritiesController {

    @FXML
    private ListView<String> priorityListView;

    @FXML
    private TextField txtPriorityName;

    private TaskManager taskManager;
    private MainController mainController;
    private ObservableList<String> priorityList;
    
    public void initializeWithTaskManager(TaskManager taskManager, MainController mainController) {
        this.taskManager = taskManager;
        this.mainController = mainController;
        priorityList = FXCollections.observableArrayList(taskManager.getPriorities());
        priorityListView.setItems(priorityList);
    }

    @FXML
    private void onAddPriority() {
        String priorityName = txtPriorityName.getText();
        if (priorityName.isEmpty()) {
            showAlert("Error", "Priority name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.addPriority(priorityName);
        updatePriorityList();
        txtPriorityName.clear();
    }

    @FXML
    private void onEditPriority() {
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority == null) {
            showAlert("Error", "No priority selected.", Alert.AlertType.ERROR);
            return;
        }
        if (selectedPriority.equals("Default")) {
            showAlert("Error", "Cannot edit default priority.", Alert.AlertType.ERROR);
            return;
        }
        

        TextInputDialog dialog = new TextInputDialog(selectedPriority);
        dialog.setTitle("Edit Priority");
        dialog.setHeaderText("Edit Priority");
        dialog.setContentText("Enter new priority name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPriority -> {
            if (newPriority.isEmpty()) {
                showAlert("Error", "New priority name cannot be empty.", Alert.AlertType.ERROR);
            } else {
                taskManager.editPriority(selectedPriority, newPriority);
                updatePriorityList();
            }
        });
    }

    @FXML
    private void onDeletePriority() {
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority == null || selectedPriority.equals("Default")) {
            showAlert("Error", "Cannot delete default priority or no priority selected.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.deletePriority(selectedPriority);
        updatePriorityList();
    }
    
    private void updatePriorityList() {
    	priorityList.setAll(taskManager.getPriorities());
        mainController.refreshLists();
    }


    @FXML
    private void onClose() {
        Stage stage = (Stage) priorityListView.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
