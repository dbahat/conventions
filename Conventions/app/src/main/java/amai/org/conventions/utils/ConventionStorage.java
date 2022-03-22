package amai.org.conventions.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Update;
import amai.org.conventions.model.conventions.Convention;
import androidx.annotation.RawRes;

public class ConventionStorage {
	private static final String TAG = ConventionStorage.class.getCanonicalName();

	private static final String EVENT_USER_INPUT_FILE_NAME = "convention_data_user_input.json";
	private static final String CONVENTION_FEEDBACK_FILE_NAME = "convention_feedback.json";
	private static final String UPDATES_FILE_NAME = "convention_updates.json";
	private static final String EVENTS_FILE_NAME = "convention_events.json";

	private static final ReentrantReadWriteLock filesystemAccessLock = new ReentrantReadWriteLock();

	private static Context context;
	private final Convention convention;
	private boolean hasInitialEventsFile;
	private int initialEventsFileResource = 0;
	private int eventsFileVersion = 0;

	public ConventionStorage(Convention convention, @RawRes int initialEventsFile, int eventsFileVersion) {
		this.convention = convention;
		this.hasInitialEventsFile = true;
		this.initialEventsFileResource = initialEventsFile;
		this.eventsFileVersion = eventsFileVersion;
	}

	public ConventionStorage(Convention convention) {
		this.convention = convention;
		hasInitialEventsFile = false;
	}

	private String getConventionFileName(String file) {
		return convention.getId() + "_" + file;
	}

	private String getConventionFeedbackFileName() {
		return getConventionFileName(CONVENTION_FEEDBACK_FILE_NAME);
	}

	private String getEventUserInputFileName() {
		return getConventionFileName(EVENT_USER_INPUT_FILE_NAME);
	}

	private String getUpdatesFileName() {
		return getConventionFileName(UPDATES_FILE_NAME);
	}

	private String getEventsFileName() {
		String fileName = getConventionFileName(EVENTS_FILE_NAME);
		if (eventsFileVersion > 0) {
			fileName = eventsFileVersion + "_" + fileName;
		}
		return fileName;
	}

	public void saveUserInput() {
		Map<String, ConventionEvent.UserInput> origUserInput = convention.getUserInput();
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

		savePrivateFile(createGsonSerializer().toJson(userInput), getEventUserInputFileName());
	}

	private static Gson createGsonSerializer() {
		return new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateAdapter())
				// Save smiley answers according to enum value name instead of toString()
				.registerTypeAdapter(FeedbackQuestion.Smiley3PointAnswer.class, new EnumSerializer<>())
				.registerTypeAdapter(FeedbackQuestion.Smiley5PointAnswer.class, new EnumSerializer<>())
				.create();
	}

	private static Gson createGsonDeserializer() {
		return new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateAdapter())
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.create();
	}

	public void saveConventionFeedback() {
		Survey feedback = convention.getFeedback();
		try {
			feedback = feedback.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		feedback.removeUnansweredQuestions();
		savePrivateFile(createGsonSerializer().toJson(feedback), getConventionFeedbackFileName());
	}

	public void saveUpdates() {
		String updatesJson = createGsonSerializer().toJson(convention.getUpdates());
		saveCacheFile(updatesJson, getUpdatesFileName());
	}

	public void saveEvents() {
		String eventsString = createGsonSerializer().toJson(convention.getEvents());
		filesystemAccessLock.writeLock().lock();
		try {
			saveCacheFile(eventsString, getEventsFileName());
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

	public void initFromFile(Context context) {
		ConventionStorage.context = context;

		// First get the convention events data (before reading the user input, as it requires us to have the events set).
		if (!tryReadEventsFromCache()) {
			readEventsFromLocalResources();
		}

		readUserInputFromFile();
		readConventionFeedbackFromFile();
		readUpdatesFromFile();
	}

	private boolean tryReadEventsFromCache() {
		List<ConventionEvent> events = readJsonFromCacheFile(new TypeToken<List<ConventionEvent>>() {
		}.getType(), getEventsFileName());
		if (events == null) {
			// Since we cannot read the cache file, delete it so we won't try to read it again next time
			tryDeleteCacheFile(getEventsFileName());
			return false;
		}
		convention.setEvents(events);
		return true;
	}

	private static void tryDeleteCacheFile(String fileName) {
		File file = new File(context.getCacheDir(), fileName);
		if (file.exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		}
	}

	private void readEventsFromLocalResources() {
		if (!hasInitialEventsFile) {
			throw new RuntimeException("Convention " + convention.getId() + " has no initial events file");
		}

		// No need to lock here, since we only read from the resources and only during app launch.
		List<ConventionEvent> events = null;
		try {
			events = readJsonAndClose(new TypeToken<List<ConventionEvent>>() {
					}.getType(),
					context.getResources().openRawResource(initialEventsFileResource));
		} catch (Exception e) {
			Log.e(TAG, "Could not read initial application events cache", e);
		}

		// We will load it from the internet if possible
		if (events == null) {
			events = new ArrayList<>();
		}
		Convention.getInstance().setEvents(events);
	}

	private void readUserInputFromFile() {
		Map<String, ConventionEvent.UserInput> result =
				readJsonFromFile(new TypeToken<Map<String, ConventionEvent.UserInput>>() {
				}.getType(), getEventUserInputFileName());
		if (result == null) {
			return;
		}

		Map<String, ConventionEvent.UserInput> currentUserInput = convention.getUserInput();
		for (Map.Entry<String, ConventionEvent.UserInput> entry : result.entrySet()) {
			ConventionEvent.UserInput currentInput = currentUserInput.get(entry.getKey());
			// Ignore non-existing events
			if (currentInput != null) {
				currentInput.updateFrom(entry.getValue());
				convention.convertUserInputForEvent(currentInput, convention.findEventById(entry.getKey()));
			}
		}
	}

	private void readConventionFeedbackFromFile() {
		Survey result = readJsonFromFile(Survey.class, getConventionFeedbackFileName());
		if (result == null) {
			return;
		}

		Survey currentFeedback = convention.getFeedback();
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

	private void readUpdatesFromFile() {
		List<Update> updates = readJsonFromCacheFile(new TypeToken<List<Update>>() {
		}.getType(), getUpdatesFileName());
		if (updates == null) {
			return;
		}
		convention.setUpdates(updates);
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
			File file = new File(context.getCacheDir(), fileName);
			if (file.exists()) {
				return new FileInputStream(file);
			} else {
				Log.i(TAG, "Cache file " + fileName + " does not exist");
				return null;
			}
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

	private static class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			String formatted = Dates.formatDate("yyyy-MM-dd'T'HH:mm:ss", Dates.localToUTCTime(src));
			return new JsonPrimitive(formatted);
		}

		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String formattedDate = json.getAsString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Dates.getLocale());
			try {
				Date utcDate = sdf.parse(formattedDate);
				return Dates.utcToLocalTime(utcDate);
			} catch (ParseException e) {
				throw new JsonParseException("Date cannot be parsed", e);
			}
		}
	}
}
