package model;

import java.time.LocalDate;

public class Reminder {
    private String type; // e.g., "One Day Before", "One Week Before", "One Month Before", "Custom"
    private LocalDate reminderDate;

    public Reminder(String type, LocalDate reminderDate) {
        this.type = type;
        this.reminderDate = reminderDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    @Override
    public String toString() {
        return type + " - " + reminderDate;
    }
}
