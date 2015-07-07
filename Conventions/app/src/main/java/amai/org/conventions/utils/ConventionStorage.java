package amai.org.conventions.utils;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Update;

public class ConventionStorage {
    private static final String EVENT_USER_INPUT_FILE_NAME = "convention_data_user_input";
    private static final String UPDATES_FILE_NAME = "convention_updates";
    private static final String EVENTS_FILE_NAME = "convention_events";

    private static ReentrantReadWriteLock filesystemAccessLock = new ReentrantReadWriteLock();

    private static Context context;

    public void saveUserInput() {
        // Gather all event user input in a list with the event id
        List<ConventionEvent> events = Convention.getInstance().getEvents();
        Map<String, ConventionEvent.UserInput> userInput = new HashMap<>(events.size());
        for (ConventionEvent event : events) {
            userInput.put(event.getId(), event.getUserInput());
        }

        saveFile(userInput, EVENT_USER_INPUT_FILE_NAME);
    }

    public void saveUpdates() {
        saveFile(Convention.getInstance().getUpdates(), UPDATES_FILE_NAME);
    }

    public void saveEvents() {
        filesystemAccessLock.writeLock().lock();
        try {
            saveFile(Convention.getInstance().getEvents(), EVENTS_FILE_NAME);
        }
        finally {
            filesystemAccessLock.writeLock().unlock();
        }
    }

    public static void initFromFile(Context context) {
        ConventionStorage.context = context;

        // First get the convention events data (before reading the user input, as it requires us to have the events set).
        if (!tryReadEventsFromCache()) {
            readEventsFromLocalResources();
        };

        readUserInputFromFile();
        readUpdatesFromFile();
    }

    private static boolean tryReadEventsFromCache() {
        Object result = readFile(EVENTS_FILE_NAME);
        if (result == null) {
            return false;
        }

        @SuppressWarnings("unchecked")
        List<ConventionEvent> events = (List<ConventionEvent>) result;
        Convention.getInstance().setEvents(events);
        return true;
    }

    private static void readEventsFromLocalResources() {
        // No need to lock here, since we only read from the resources and only during app launch.
        Object result = readResource(EVENTS_FILE_NAME);
        if (result == null) {
            throw new AssertionError("The initial application events cache is missing");
        }

        @SuppressWarnings("unchecked")
        List<ConventionEvent> events = (List<ConventionEvent>) result;
        Convention.getInstance().setEvents(events);
    }

    private static void readUserInputFromFile() {
        filesystemAccessLock.readLock().lock();
        Object result = null;
        try {
            result = readFile(EVENT_USER_INPUT_FILE_NAME);
        }
        finally {
            filesystemAccessLock.readLock().unlock();
        }

        if (result == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, ConventionEvent.UserInput> userInput = (Map<String, ConventionEvent.UserInput>) result;

        List<ConventionEvent> events = Convention.getInstance().getEvents();
        for (ConventionEvent event : events) {
            ConventionEvent.UserInput currInput = userInput.get(event.getId());
            if (currInput != null) {
                event.setUserInput(currInput);
            }
        }
    }

    private void saveFile(Object objectToSave, String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(objectToSave);
            os.close();
            fos.close();

        } catch (IOException e) {
            // Nothing we can do... don't crash the app. Maybe show error message?
            e.printStackTrace();
        }
    }

    private static void readUpdatesFromFile() {
        Object result = readFile(UPDATES_FILE_NAME);
        if (result == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Update> updates = (List<Update>) result;
        Convention.getInstance().setUpdates(updates);
    }

    private static Object readFile(String fileName) {
        try {
            return readFile(context.openFileInput(fileName));
        } catch (FileNotFoundException f) {
            // Ignore - default user input will be created from hard-coded data
            return null;
        }
    }

    private static Object readResource(String fileName) {
        try {
            return readFile(context.getAssets().open(fileName));
        } catch (IOException e) {
            // Ignore - default user input will be created from hard-coded data
            return null;
        }
    }

    private static Object readFile(InputStream inputStream) {
        Object result = null;

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            result = objectInputStream.readObject();
            objectInputStream.close();
            inputStream.close();

        } catch (Exception e) {
            // Nothing we can do about badly formatted file, don't crash the app.
            e.printStackTrace();
        }

        return result;
    }
}
