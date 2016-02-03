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
import java.io.OutputStream;
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
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Update;

public class ConventionStorage {
	private static final String TAG = ConventionStorage.class.getCanonicalName();

    private static final String EVENT_USER_INPUT_FILE_NAME = "convention_data_user_input.json";
	private static final String CONVENTION_FEEDBACK_FILE_NAME = "convention_feedback.json";
    private static final String UPDATES_FILE_NAME = "convention_updates.json";
    private static final String EVENTS_FILE_NAME = "convention_events.json";

    private static ReentrantReadWriteLock filesystemAccessLock = new ReentrantReadWriteLock();

    private static Context context;

    public void saveUserInput() {
	    Map<String, ConventionEvent.UserInput> origUserInput = Convention.getInstance().getUserInput();
	    Map<String, ConventionEvent.UserInput> userInput = new LinkedHashMap<>();
	    for (Map.Entry<String, ConventionEvent.UserInput> entry : origUserInput.entrySet()) {
		    ConventionEvent.UserInput input = entry.getValue();
		    if (input.isAttending()
					|| input.getFeedback().hasAnsweredQuestions()
					|| input.getEventAboutToStartNotification().isEnabled()
					|| input.getEventFeedbackReminderNotification().isEnabled()) {
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

	    savePrivateFile(createGsonSerializer().toJson(userInput), EVENT_USER_INPUT_FILE_NAME);
    }

	private static Gson createGsonSerializer() {
		return new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				// Save Smiley3PointAnswer according to enum value name instead of toString()
				.registerTypeAdapter(FeedbackQuestion.Smiley3PointAnswer.class, new EnumSerializer<>())
				.create();
	}

	private static Gson createGsonDeserializer() {
		return new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.create();
	}

	public void saveConventionFeedback() {
		Feedback feedback = Convention.getInstance().getFeedback();
		try {
			feedback = feedback.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		feedback.removeUnansweredQuestions();
		savePrivateFile(createGsonSerializer().toJson(feedback), CONVENTION_FEEDBACK_FILE_NAME);
	}

    public void saveUpdates() {
	    String updatesJson = createGsonSerializer().toJson(Convention.getInstance().getUpdates());
	    saveCacheFile(updatesJson, UPDATES_FILE_NAME);
    }

    public void saveEvents() {
	    String eventsString = createGsonSerializer().toJson(Convention.getInstance().getEvents());
	    filesystemAccessLock.writeLock().lock();
        try {
            saveCacheFile(eventsString, EVENTS_FILE_NAME);
        } finally {
            filesystemAccessLock.writeLock().unlock();
        }
    }

	private void saveAndCloseTextFile(String toSave, OutputStream outputStream) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(outputStream);
		writer.write(toSave);
		writer.close();
		outputStream.close();
	}

	private void savePrivateFile(String toSave, String fileName) {
		try {
			saveAndCloseTextFile(toSave,
					context.openFileOutput(fileName, Context.MODE_PRIVATE));
		} catch (Exception e) {
			// Nothing we can do... don't crash the app.
			Log.e(TAG, "File " + fileName + " could not be saved", e);
		}
	}

	private void saveCacheFile(String objectToSave, String fileName) {
		try {
			saveAndCloseTextFile(objectToSave,
					new FileOutputStream(new File(context.getCacheDir(), fileName)));
		} catch (Exception e) {
			// Nothing we can do... don't crash the app.
			Log.e(TAG, "File " + fileName + " could not be saved to cache", e);
		}
	}

	public static void initFromFile(Context context) {
        ConventionStorage.context = context;

        // First get the convention events data (before reading the user input, as it requires us to have the events set).
        if (!tryReadEventsFromCache()) {
            readEventsFromLocalResources();
        }

        readUserInputFromFile();
		readConventionFeedbackFromFile();
        readUpdatesFromFile();
    }

    private static boolean tryReadEventsFromCache() {
	    List<ConventionEvent> events = readJsonFromCacheFile(new TypeToken<List<ConventionEvent>>() {}.getType(), EVENTS_FILE_NAME);
        if (events == null) {
			// Since we cannot read the cache file, delete it so we won't try to read it again next time
			tryDeleteCacheFile(EVENTS_FILE_NAME);
            return false;
        }
        Convention.getInstance().setEvents(events);
        return true;
    }

	private static void tryDeleteCacheFile(String fileName) {
		File file = new File(context.getCacheDir(), fileName);
		if (file.exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		}
	}

    private static void readEventsFromLocalResources() {
        // No need to lock here, since we only read from the resources and only during app launch.
	    Object result = null;
	    try {
	        result = readJsonAndClose(new TypeToken<List<ConventionEvent>>() {}.getType(),
			        context.getResources().openRawResource(R.raw.convention_events));
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
	    Map<String, ConventionEvent.UserInput> result =
			    readJsonFromFile(new TypeToken<Map<String, ConventionEvent.UserInput>>() {}.getType(), EVENT_USER_INPUT_FILE_NAME);
        if (result == null) {
            return;
        }

	    Map<String, ConventionEvent.UserInput> currentUserInput = Convention.getInstance().getUserInput();
	    for (Map.Entry<String, ConventionEvent.UserInput> entry : result.entrySet()) {
		    ConventionEvent.UserInput currentInput = currentUserInput.get(entry.getKey());
		    // Ignore non-existing events
		    if (currentInput != null) {
			    currentInput.updateFrom(entry.getValue());
		    }
	    }
    }

	private static void readConventionFeedbackFromFile() {
		Feedback result = readJsonFromFile(Feedback.class, CONVENTION_FEEDBACK_FILE_NAME);
		if (result == null) {
			return;
		}

		Feedback currentFeedback = Convention.getInstance().getFeedback();
		currentFeedback.updateFrom(result);
	}

	private static <T> T readJsonFromFile(Type type, String fileName) {
		filesystemAccessLock.readLock().lock();
		try {
			return readJsonAndClose(type, openTextFile(fileName));
		} finally {
			filesystemAccessLock.readLock().unlock();
		}
	}

	private static void readUpdatesFromFile() {
		List<Update> updates = readJsonFromCacheFile(new TypeToken<List<Update>>() {}.getType(), UPDATES_FILE_NAME);
        if (updates == null) {
            return;
        }
        Convention.getInstance().setUpdates(updates);
    }

	private static InputStream openTextFile(String fileName) {
		try {
			return context.openFileInput(fileName);
		} catch (Exception e) {
			// Ignore - default user input will be created from hard-coded data
			Log.i(TAG, "Could not read file " + fileName + ": " + e.getMessage());
			return null;
		}
	}

	private static <T> T readJsonFromCacheFile(Type type, String fileName) {
		filesystemAccessLock.readLock().lock();
		try {
			return readJsonAndClose(type, openCacheTextFile(fileName));
		} finally {
			filesystemAccessLock.readLock().unlock();
		}
	}

	private static <T> T readJsonAndClose(Type type, InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}

		T result = null;
		Reader reader = null;
		try {
			reader = new InputStreamReader(inputStream);
			result = createGsonDeserializer().fromJson(reader, type);
		} catch (Exception e) {
			Log.e(TAG, "Could not deserialize file of type " + type.toString() + ": " + e.getMessage());
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				// Nothing we can do about it
			}
		}
		return result;
	}

	private static InputStream openCacheTextFile(String fileName) {
		try {
			return new FileInputStream(new File(context.getCacheDir(), fileName));
		} catch (Exception e) {
			// Ignore - default user input will be created from hard-coded data
			Log.i(TAG, "Could not read cache file " + fileName + ": " + e.getMessage());
			return null;
		}
	}

	private static class EnumSerializer<T extends Enum> implements JsonSerializer<T> {
		@Override
		public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.name());
		}
	}
}
