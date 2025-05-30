package logic;

import data.DataManager;
import model.Task;

import java.time.LocalDate;
import java.util.*;
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
    
    public List<Task> getCompletedTasks() {
        return tasks.stream()
                .filter(task -> "Completed".equals(task.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Task> getDelayedTasks() {
        return tasks.stream()
                .filter(task -> "Delayed".equals(task.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksDueSoon() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        return tasks.stream()
                .filter(task -> task.getDeadline() != null && 
                                !task.getDeadline().isBefore(today) && 
                                task.getDeadline().isBefore(nextWeek))
                .collect(Collectors.toList());
    }

    public List<String> getCategories() {
        return new ArrayList<>(categories);
    }

    public List<String> getPriorities() {
        return new ArrayList<>(priorities);
    }

    public void addCategory(String category) {
        categories.add(category);
        saveTasksToFile();
    }
    
    public void editCategory(String oldCategory, String newCategory) {
        // Check if the old category exists and new category doesn't already exist
        if (categories.contains(oldCategory) && !categories.contains(newCategory)) {
            // Update all tasks with the old category to the new category
            tasks.forEach(task -> {
                if (task.getCategory().equals(oldCategory)) {
                    task.setCategory(newCategory);
                }
            });

            categories.remove(oldCategory);
            categories.add(newCategory);

            saveTasksToFile();
        }
    }

    public void deleteCategory(String category) {
        tasks.removeIf(task -> task.getCategory().equals(category));
        categories.remove(category);
        saveTasksToFile();
    }
    

    public void addPriority(String priority) {
        priorities.add(priority);
        saveTasksToFile();
    }
    
    public void editPriority(String oldPriority, String newPriority) {
        // Check if the old priority exists and new priority doesn't already exist
        if (priorities.contains(oldPriority) && !priorities.contains(newPriority)) {
            // Update all tasks with the old priority to the new priority
            tasks.forEach(task -> {
                if (task.getPriority().equals(oldPriority)) {
                    task.setPriority(newPriority);
                }
            });

            priorities.remove(oldPriority);
            priorities.add(newPriority);

            saveTasksToFile();
        }
    }

    public void deletePriority(String priority) {
        if (!"Default".equals(priority)) {
            tasks.forEach(task -> {
                if (task.getPriority().equals(priority)) {
                    task.setPriority("Default");
                }
            });
            priorities.remove(priority);
            saveTasksToFile();
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
    

    // Search Methods
    public List<Task> searchTasksByTitle(String query) {
        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Task> searchTasksByCategory(String query) {
        return tasks.stream()
                .filter(task -> task.getCategory() != null && task.getCategory().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Task> searchTasksByPriority(String query) {
        return tasks.stream()
                .filter(task -> task.getPriority() != null && task.getPriority().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Task> searchTasks(String query) {
        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Task> getTasksByCategory(String category) {
        return tasks.stream()
                    .filter(task -> category.equals(task.getCategory()))
                    .collect(Collectors.toList());
    }

    public void loadTasksFromFile() {
        tasks = dataManager.loadTasks();
        Map<String, Object> data = dataManager.loadData();
        Object categoriesObj = data.getOrDefault("categories", new ArrayList<>());
        categories = new HashSet<>();
        if (categoriesObj instanceof List) {
            categories.addAll(((List<?>) categoriesObj).stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList());
        }

        Object prioritiesObj = data.getOrDefault("priorities", Collections.singletonList("Default"));
        priorities = new HashSet<>();
        if (prioritiesObj instanceof List) {
            priorities.addAll(((List<?>) prioritiesObj).stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList());
        }

    }

    public void saveTasksToFile() {
        dataManager.saveTasks(tasks, new ArrayList<>(categories), new ArrayList<>(priorities));
    }
}
