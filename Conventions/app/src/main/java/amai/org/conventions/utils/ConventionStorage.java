package amai.org.conventions.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Update;

public class ConventionStorage {
	private static final String TAG = ConventionStorage.class.getCanonicalName();

    private static final String EVENT_USER_INPUT_FILE_NAME = "convention_data_user_input.json";
    private static final String UPDATES_FILE_NAME = "convention_updates";
    private static final String EVENTS_FILE_NAME = "convention_events";

    private static ReentrantReadWriteLock filesystemAccessLock = new ReentrantReadWriteLock();

    private static Context context;

    public void saveUserInput() {
	    Map<String, ConventionEvent.UserInput> origUserInput = Convention.getInstance().getUserInput();
	    Map<String, ConventionEvent.UserInput> userInput = new LinkedHashMap<>();
	    for (Map.Entry<String, ConventionEvent.UserInput> entry : origUserInput.entrySet()) {
		    ConventionEvent.UserInput input = entry.getValue();
		    if (input.isAttending() || input.getFeedback().hasAnsweredQuestions()) {
			    try {
				    // Copy the input and remove unanswered questions
				    input = input.clone();
				    input.getFeedback().removeUnansweredQuestions();
			    } catch (CloneNotSupportedException e) {
				    throw new RuntimeException(e);
			    }
			    userInput.put(entry.getKey(), input);
		    }
	    }

	    // Save Smiley3PointAnswer according to enum value name instead of toString()
	    Gson serialzer = new GsonBuilder().registerTypeAdapter(FeedbackQuestion.Smiley3PointAnswer.class,
			    new EnumSerializer<>()).create();
	    saveTextFile(serialzer.toJson(userInput), EVENT_USER_INPUT_FILE_NAME);
    }

    public void saveUpdates() {
        saveCacheFile(Convention.getInstance().getUpdates(), UPDATES_FILE_NAME);
    }

    public void saveEvents() {
        filesystemAccessLock.writeLock().lock();
        try {
            saveCacheFile(Convention.getInstance().getEvents(), EVENTS_FILE_NAME);
        } finally {
            filesystemAccessLock.writeLock().unlock();
        }
    }

	private void saveTextFile(String toSave, String fileName) {
		try {
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			OutputStreamWriter writer = new OutputStreamWriter(fos);
			writer.write(toSave);
			writer.close();
			fos.close();
		} catch (Exception e) {
			// Nothing we can do... don't crash the app.
			Log.e(TAG, "File " + fileName + " could not be saved", e);
		}
	}

	private void saveCacheFile(Object objectToSave, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(context.getCacheDir(), fileName));
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(objectToSave);
			os.close();
			fos.close();
		} catch (Exception e) {
			// Nothing we can do... don't crash the app.
			Log.e(TAG, "File " + fileName + " could not be saved", e);
		}
	}

	public static void initFromFile(Context context) {
        ConventionStorage.context = context;

        // First get the convention events data (before reading the user input, as it requires us to have the events set).
        if (!tryReadEventsFromCache()) {
            readEventsFromLocalResources();
        }

        readUserInputFromFile();
        readUpdatesFromFile();
    }

    private static boolean tryReadEventsFromCache() {
        Object result = readCacheFile(EVENTS_FILE_NAME);
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
	    Object result = null;
	    try {
	        result = readFile(context.getResources().openRawResource(R.raw.convention_events));
	    } catch (Exception e) {
		    Log.e(TAG, "Could not read initial application events cache", e);
	    }

	    // We will load it from the internet if possible
        if (result == null) {
	        result = new ArrayList<ConventionEvent>();
        }

        @SuppressWarnings("unchecked")
        List<ConventionEvent> events = (List<ConventionEvent>) result;
        Convention.getInstance().setEvents(events);
    }

    private static void readUserInputFromFile() {
        filesystemAccessLock.readLock().lock();
	    Reader reader = null;
	    Object result = null;
        try {
	        reader = openTextFile(EVENT_USER_INPUT_FILE_NAME);
	        if (reader != null) {
		        result = new Gson().fromJson(reader, new TypeToken<Map<String, ConventionEvent.UserInput>>() {}.getType());
	        }
        } finally {
	        if (reader != null) {
		        try {
			        reader.close();
		        } catch (IOException e) {
			        // Nothing we can do about it
		        }
	        }
            filesystemAccessLock.readLock().unlock();
        }

        if (result == null) {
            return;
        }

	    Map<String, ConventionEvent.UserInput> currentUserInput = Convention.getInstance().getUserInput();
	    @SuppressWarnings("unchecked")
        Map<String, ConventionEvent.UserInput> userInput = (Map<String, ConventionEvent.UserInput>) result;
	    for (Map.Entry<String, ConventionEvent.UserInput> entry : userInput.entrySet()) {
		    ConventionEvent.UserInput currentInput = currentUserInput.get(entry.getKey());
		    // Ignore non-existing events
		    if (currentInput != null) {
			    currentInput.updateFrom(entry.getValue());
		    }
	    }
    }

    private static void readUpdatesFromFile() {
        Object result = readCacheFile(UPDATES_FILE_NAME);
        if (result == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Update> updates = (List<Update>) result;
        Convention.getInstance().setUpdates(updates);
    }

	private static Reader openTextFile(String fileName) {
		try {
			FileInputStream inputStream = context.openFileInput(fileName);
			return new InputStreamReader(inputStream);
		} catch (Exception e) {
			// Ignore - default user input will be created from hard-coded data
			Log.i(TAG, "Could not read file " + fileName + ": " + e.getMessage());
			return null;
		}
	}

    private static Object readCacheFile(String fileName) {
        try {
            return readFile(new FileInputStream(new File(context.getCacheDir(), fileName)));
        } catch (Exception e) {
            // Ignore - required file will be created from default hard-coded data
	        Log.i(TAG, "Could not read file " + fileName + ": " + e.getMessage());
            return null;
        }
    }

	private static Object readFile(InputStream inputStream) throws Exception {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

		Object result = objectInputStream.readObject();
        objectInputStream.close();
        inputStream.close();

        return result;
    }

	private static class EnumSerializer<T extends Enum> implements JsonSerializer<T> {
		@Override
		public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.name());
		}
	}
}
