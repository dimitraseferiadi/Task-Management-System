package logic;

import data.DataManager;
import model.Task;

import java.time.LocalDate;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private Set<String> categories = new HashSet<>();
    private Set<String> priorities = new HashSet<>(Collections.singleton("Default"));
    private final DataManager dataManager;

    public TaskManager() {
        this.dataManager = new DataManager();
        loadTasksFromFile();
    }

    // Task Management
    public void addTask(Task task) {
        tasks.add(task);
        saveTasksToFile();
    }

    public void updateTask(Task updatedTask) {
        tasks = tasks.stream()
                .map(task -> task.getId().equals(updatedTask.getId()) ? updatedTask : task)
                .collect(Collectors.toList());
        saveTasksToFile();
    }

    public void deleteTask(Task task) {
        tasks.removeIf(t -> t.getId().equals(task.getId()));
        saveTasksToFile();
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public List<String> getCategories() {
        return new ArrayList<>(categories);
    }

    public List<String> getPriorities() {
        return new ArrayList<>(priorities);
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public void deleteCategory(String category) {
        tasks.removeIf(task -> task.getCategory().equals(category));
        categories.remove(category);
    }

    public void addPriority(String priority) {
        priorities.add(priority);
    }

    public void deletePriority(String priority) {
        if (!"Default".equals(priority)) {
            tasks.forEach(task -> {
                if (task.getPriority().equals(priority)) {
                    task.setPriority("Default");
                }
            });
            priorities.remove(priority);
        }
    }

    public int getCompletedTaskCount() {
        return (int) tasks.stream().filter(task -> "Completed".equals(task.getStatus())).count();
    }

    public int getDelayedTaskCount() {
        return (int) tasks.stream().filter(task -> "Delayed".equals(task.getStatus())).count();
    }

    public int getTasksDueSoonCount() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        return (int) tasks.stream()
                .filter(task -> task.getDeadline().isAfter(today) && task.getDeadline().isBefore(nextWeek))
                .count();
    }

    public List<Task> searchTasks(String query) {
        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void loadTasksFromFile() {
        tasks = dataManager.loadTasks();
    }

    public void saveTasksToFile() {
        dataManager.saveTasks(tasks);
    }
}
