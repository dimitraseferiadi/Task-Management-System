package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Task;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


public class DataManager {
    private final File taskFile = new File("resources/medialab_data.json");
    private final Gson gson;

    public DataManager() {
        gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    }

    public List<Task> loadTasks() {
        if (!taskFile.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(taskFile)) {
            Type taskListType = new TypeToken<List<Task>>() {}.getType();
            return gson.fromJson(reader, taskListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveTasks(List<Task> tasks) {
        try (FileWriter writer = new FileWriter(taskFile)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

