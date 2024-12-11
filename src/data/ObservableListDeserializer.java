package data;

import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Reminder;

import java.lang.reflect.Type;

public class ObservableListDeserializer implements JsonDeserializer<ObservableList<Reminder>> {

    @Override
    public ObservableList<Reminder> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ObservableList<Reminder> reminders = FXCollections.observableArrayList();
        if (json.isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray()) {
                Reminder reminder = context.deserialize(element, Reminder.class);
                reminders.add(reminder);
            }
        }
        return reminders;
    }
}
