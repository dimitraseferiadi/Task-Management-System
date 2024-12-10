package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.TaskManager;

import java.util.Optional;


public class CategoriesController {

    @FXML
    private ListView<String> categoryListView;

    @FXML
    private TextField txtCategoryName;

    private TaskManager taskManager;
    private MainController mainController;
    private ObservableList<String> categoryList;
    
    public void initializeWithTaskManager(TaskManager taskManager, MainController mainController) {
        this.taskManager = taskManager;
        this.mainController = mainController;
        categoryList = FXCollections.observableArrayList(taskManager.getCategories());
        categoryListView.setItems(categoryList);
    }

    @FXML
    private void onAddCategory() {
        String categoryName = txtCategoryName.getText();
        if (categoryName.isEmpty()) {
            showAlert("Error", "Category name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.addCategory(categoryName);
        updateCategoryList();
        txtCategoryName.clear();
    }

    @FXML
    private void onEditCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert("Error", "No category selected.", Alert.AlertType.ERROR);
            return;
        }
        if (selectedCategory.equals("Default")) {
            showAlert("Error", "Cannot edit default category.", Alert.AlertType.ERROR);
            return;
        }
        

        TextInputDialog dialog = new TextInputDialog(selectedCategory);
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Edit Category");
        dialog.setContentText("Enter new category name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newCategory -> {
            if (newCategory.isEmpty()) {
                showAlert("Error", "New category name cannot be empty.", Alert.AlertType.ERROR);
            } else {
                taskManager.editCategory(selectedCategory, newCategory);
                updateCategoryList();
            }
        });
    }

    @FXML
    private void onDeleteCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert("Error", "No category selected.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.deleteCategory(selectedCategory);
        updateCategoryList();
    }
    
    private void updateCategoryList() {
    	categoryList.setAll(taskManager.getCategories());
        mainController.refreshLists();
    }


    @FXML
    private void onClose() {
        Stage stage = (Stage) categoryListView.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
