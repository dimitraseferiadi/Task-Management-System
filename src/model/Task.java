package model;

import java.time.LocalDate;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Task {
    private String id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private LocalDate deadline;
    private String status;
    private ObservableList<Reminder> reminders;

    public Task(String title, String description, String category, String priority, LocalDate deadline, String status) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
        this.status = "Open";
        this.reminders = FXCollections.observableArrayList();
    }
    
    @Override
    public String toString() {
        return title;
    }
    
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    
    public ObservableList<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders.setAll(reminders);
    }

    public void addReminder(Reminder reminder) {
    	this.reminders.add(reminder);
    }

    public void removeReminder(Reminder reminder) {
        this.reminders.remove(reminder);
    }

    public void clearRemindersIfCompleted() {
        if ("Completed".equalsIgnoreCase(status)) {
            reminders.clear();
        }
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
