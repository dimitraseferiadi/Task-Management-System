package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Task;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class DataManager {
    private static final String taskFile = "resources/medialab_data.json";
    private final Gson gson;

    public DataManager() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    public Map<String, Object> loadData() {
        if (!Files.exists(Paths.get(taskFile))) {
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(taskFile)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public List<Task> loadTasks() {
        Map<String, Object> data = loadData();
        if (data.containsKey("tasks")) {
            // Deserialize tasks using a TypeToken for List<Task>
            Type taskListType = new TypeToken<List<Task>>() {}.getType();
            return gson.fromJson(gson.toJson(data.get("tasks")), taskListType);
        }
        return new ArrayList<>();
    }

    public void saveData(Map<String, Object> data) {
        try (FileWriter writer = new FileWriter(taskFile)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTasks(List<Task> tasks, List<String> categories, List<String> priorities) {
        Map<String, Object> data = new HashMap<>();
        data.put("tasks", tasks);
        data.put("categories", categories);
        data.put("priorities", priorities);
        saveData(data);
    }
}
