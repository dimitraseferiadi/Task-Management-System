package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import logic.TaskManager;


import gui.MainController;

public class Main extends Application {

    private TaskManager taskManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Scene scene = new Scene(loader.load());

            // Get the screen width and height
            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

            // Set the scene to use a portion of the screen size
            primaryStage.setWidth(screenWidth * 0.8);
            primaryStage.setHeight(screenHeight * 0.9);

            // Center the stage on the screen
            primaryStage.setX((screenWidth - primaryStage.getWidth()) / 2);
            primaryStage.setY((screenHeight - primaryStage.getHeight()) / 2);

            primaryStage.setScene(scene);

            // Pass the TaskManager to the controller
            MainController controller = loader.getController();
            TaskManager taskManager = new TaskManager(); // Initialize TaskManager
            controller.setTaskManager(taskManager);
            
            primaryStage.setTitle("MediaLab Task Manager");
            primaryStage.show();

            // Save TaskManager reference for shutdown handling
            this.taskManager = taskManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Save data on application stop
        if (taskManager != null) {
            taskManager.saveTasksToFile();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
