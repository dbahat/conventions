package amai.org.conventions.networking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.BuildConfig;
import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HttpConnectionCreator;
import amai.org.conventions.utils.Log;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import amai.org.conventions.BuildConfig;
import amai.org.conventions.R;

public class ModelRefresher {
	private static final String TAG = ModelRefresher.class.getCanonicalName();

	private static final long MINIMUM_REFRESH_TIME = Dates.MILLISECONDS_IN_HOUR;

	public interface OnModelRefreshFinishedListener {
		/** Called after refresh from server was completed but before showing the updated favorite events popup, in both success and error cases */
		default void onRefreshFinished(Exception e) {}
		/** Called after refresh finished and popup was displayed when successful */
		default void onSuccess() {}
		/** Called after refresh finished when an error occured */
		default void onError(Exception error) {}
	}

	private static ModelRefresher instance = null;
	private boolean isRefreshingModel = false;

	public static synchronized ModelRefresher getInstance() {
		if (instance == null) {
			instance = new ModelRefresher();
		}
		return instance;
	}

	private ModelRefresher() {
	}

	public boolean isRefreshingModel() {
		return isRefreshingModel;
	}

	/**
	 * Downloads the events model from the server and updates it.
	 *
	 */
	public void refreshFromServer(boolean force, OnModelRefreshFinishedListener listener) {
		if (!force) {
			// Don't download if the convention is over (there won't be any more updates to the events...)
			if (Convention.getInstance().hasEnded()) {
				listener.onSuccess();
				return;
			}
			// Also don't download if we recently updated the events
			Date lastUpdate = ConventionsApplication.settings.getLastEventsUpdateDate();
			if (lastUpdate != null && Dates.now().getTime() - lastUpdate.getTime() < MINIMUM_REFRESH_TIME) {
				listener.onSuccess();
				return;
			}
		}

		isRefreshingModel = true;

		new AsyncTask<Void, Void, Exception>() {
			private String changedFavoritesText = null;

			@Override
			protected Exception doInBackground(Void... voids) {
				try {
					HttpURLConnection request = HttpConnectionCreator.createConnection(Convention.getInstance().getModelURL());
					request.connect();
					try (InputStreamReader reader = new InputStreamReader((InputStream) request.getContent())) {
						List<ConventionEvent> eventList = Convention.getInstance().getModelParser().parse(reader);

						Context context = ConventionsApplication.getCurrentContext();
						if (context != null) {
							changedFavoritesText = getChangedFavoritesText(context, Convention.getInstance().getEvents(), eventList);
						}
						if (BuildConfig.DEBUG) {
							notifyIfEventsUpdated(Convention.getInstance().getEvents(), eventList);
						}

						rescheduleChangedEventNotifications(Convention.getInstance().getEvents(), eventList);

						Convention.getInstance().setEvents(eventList);
						ConventionsApplication.settings.setLastEventsUpdatedDate();
					} finally {
						request.disconnect();
					}

					Convention.getInstance().getStorage().saveEvents();
				} catch (IOException e) {
					Log.i(TAG, "Could not retrieve model due to IOException: " + e.getMessage());
					return e;
				} catch (Exception e) {
					Log.e(TAG, "Could not retrieve model: " + e.getMessage(), e);
					return e;
				} finally {
					isRefreshingModel = false;
				}

				return null;
			}

			@Override
			protected void onPostExecute(Exception exception) {
				listener.onRefreshFinished(exception);

				SharedPreferences sharedPreferences = ConventionsApplication.settings.getSharedPreferences();
				if (sharedPreferences.getBoolean(Convention.getInstance().getId().toLowerCase() + "_favorite_event_changes", false)) {
					showFavoritesChangedMessage(changedFavoritesText);
				}

				if (exception == null) {
					listener.onSuccess();
				} else {
					listener.onError(exception);
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void showFavoritesChangedMessage(String message) {
		// If we're in some screen in the app show alert dialog with the message
		Context context = ConventionsApplication.getCurrentContext();
		if (message == null || context == null) {
			return;
		}

		boolean showGoToMyEvents = !(context instanceof MyEventsActivity);

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setTitle(context.getString(R.string.favorites_were_updated))
				.setMessage(message)
				.setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss());
		if (showGoToMyEvents) {
			builder.setNeutralButton(R.string.go_to_my_events, (dialog, which) -> {
				dialog.dismiss();
				context.startActivity(new Intent(context, MyEventsActivity.class));
			});
		}
		builder.setCancelable(true).show();
	}

	private void rescheduleChangedEventNotifications(List<ConventionEvent> currentEvents, List<ConventionEvent> newEvents) {
		Log.i(TAG, "Events refresh: Rescheduling alarms for events whose start or end time changed");
		Map<String, ConventionEvent> currentEventsById = new HashMap<>();
		for (ConventionEvent event : currentEvents) {
			currentEventsById.put(event.getId(), event);
		}

		for (ConventionEvent event : newEvents) {
			ConventionEvent currentEvent = currentEventsById.get(event.getId());
			if (currentEvent == null) {
				// If this is a new event, the current event would be null, and there are no alarms to schedule
				continue;
			}
			ConventionsApplication.getAppContext().rescheduleChangedEventAlarms(event, currentEvent);
		}
		Log.i(TAG, "Events refresh: finished updating alarms");
	}

	private String getChangedFavoritesText(Context context, List<ConventionEvent> currentEvents, List<ConventionEvent> newEvents) {
		Map<String, ConventionEvent> newEventsById = new HashMap<>();
		for (ConventionEvent event : newEvents) {
			newEventsById.put(event.getId(), event);
		}

		List<String> changedEventMessages = new LinkedList<>();
		for (ConventionEvent event : currentEvents) {
			// Only show messages for favorite events that didn't start yet
			if (event.isAttending() && !event.hasStarted()) {
				// Check if a favorite event was deleted
				if (!newEventsById.containsKey(event.getId())) {
					// Event was cancelled
					String eventMessage = getCancelledEventMessage(context, event);
					changedEventMessages.add(eventMessage);
					continue;
				}

				ConventionEvent newEvent = newEventsById.get(event.getId());
				if (newEvent == null) {
					// Shouldn't happen
					continue;
				}

				String eventMessage = getChangedEventMessage(context, event, newEvent);
				if (eventMessage != null) {
					changedEventMessages.add(eventMessage);
				}
			}
		}

		if (changedEventMessages.size() > 0) {
			return "- " + TextUtils.join("\n- ", changedEventMessages);
		}

		return null;
	}

	@VisibleForTesting
	public String getCancelledEventMessage(Context context, ConventionEvent event) {
		// Construct the message
		// האירוע 'כותרת' שהיה אמור להתקיים ב<זמן התחלה> ב<אולם> בוטל.

		// Examples of updates sent in previous conventions:
		// המשחק 'סוף העולם' שהיה אמור להתקיים ברביעי, 22.9 בשעה 20:00 בעירוני 5 בוטל
		String oldStartTime = getTimeMessage(context, event.getStartTime(), false, null);
		String oldHall = event.getHall().getName();
		return context.getString(R.string.deleted_event_message, event.getTitle(), oldStartTime, oldHall);
	}

	@VisibleForTesting
	public String getChangedEventMessage(Context context, ConventionEvent event, ConventionEvent newEvent) {
		// There are 4 important changes we notify users about: time change, hall change, duration change and
		// location type change (if we know the previous location type is not available anymore).
		boolean notifyStartTimeChanged = newEvent.getStartTime().getTime() != event.getStartTime().getTime();
		boolean notifySurationChanged = newEvent.getEndTime().getTime() - newEvent.getStartTime().getTime() !=
				event.getEndTime().getTime() - event.getStartTime().getTime();
		boolean notifyHallChanged = !newEvent.getHall().getName().equals(event.getHall().getName());

		int eventLocationType = getEventLocationTypeString(event);
		int newEventLocationType = getEventLocationTypeString(newEvent);
		// Only notify if changed from physical to virtual or from virtual to physical. If there was no
		// previous info or it became hybrid it isn't important for the user.
		boolean notifyLocationTypeChanged = eventLocationType != 0 && newEventLocationType != 0 && newEventLocationType != R.string.hybrid_event &&
				eventLocationType != newEventLocationType;

		if (!notifyStartTimeChanged && !notifySurationChanged && !notifyHallChanged && !notifyLocationTypeChanged) {
			return null;
		}

		// Construct the message
		// הארוע 'כותרת' שהיה אמור להיות [סוג האירוע] ב<זמן התחלה> ב<אולם> [באורך <אורך ישן>] <מידע חדש>.
		// Show day name only if not today. If today, only show time in the old event details (since the message will always be shown in the day of update).
		// New info: only show changed details. If new time is in the same date don't show day name. Show "today" if new date is today.

		// Examples of updates sent in previous conventions:
		// הארוע 'מסע בין כוכבים: הדורות הבאים' שהיה אמור להיות ביום חמישי בשעה 16:00 באשכול 2 עבר לשעה 20:00 באשכול 1.
		// ההרצאה 'ברוכים הבאים לסוף העולם' ביום רביעי ב-12:00 שהייתה צריכה להיות בסינמטק 5 עברה לסינמטק 4.

		// We always show the event's old time and hall. We also show the old location type and duration if they were changed.
		String oldStartTime = getTimeMessage(context, event.getStartTime(), false, null);
		String oldHall = event.getHall().getName();
		String oldLocationType = notifyLocationTypeChanged ? context.getString(eventLocationType) + " " : "";
		String oldDuration = notifySurationChanged ? context.getString(R.string.changed_duration,
				Dates.toHumanReadableTimeDuration(event.getEndTime().getTime() - event.getStartTime().getTime())) + " " : "";

		// We only show changed details
		String newInfo = null;
		if (notifyStartTimeChanged) {
			newInfo = context.getString(R.string.changed_event_new_time, getTimeMessage(context, newEvent.getStartTime(), true, event.getStartTime()));
		}

		if (notifyHallChanged) {
			if (newInfo == null) {
				newInfo = context.getString(R.string.changed_event_new_hall, newEvent.getHall().getName());
			} else {
				newInfo += " " + context.getString(R.string.changed_event_new_hall_continued, newEvent.getHall().getName());
			}
		}

		if (notifyLocationTypeChanged) {
			if (newInfo == null) {
				newInfo = context.getString(R.string.changed_event_new_location_type, context.getString(newEventLocationType));
			} else {
				newInfo += " " + context.getString(R.string.changed_event_new_location_type_continued, context.getString(newEventLocationType));
			}
		}

		if (notifySurationChanged) {
			String newDuration = Dates.toHumanReadableTimeDuration(newEvent.getEndTime().getTime() - newEvent.getStartTime().getTime());
			if (newInfo == null) {
				newInfo = context.getString(R.string.changed_event_new_duration, newDuration);
			} else if (notifyLocationTypeChanged) {
				// Avoid double "and"
				newInfo += " " + context.getString(R.string.changed_duration, newDuration);
			} else {
				newInfo += " " + context.getString(R.string.changed_event_new_duration_continued, newDuration);
			}
		}

		return context.getString(R.string.changed_event_message, event.getTitle(), oldLocationType, oldStartTime, oldHall, oldDuration, newInfo);
	}

	private @StringRes int getEventLocationTypeString(ConventionEvent event) {
		List<ConventionEvent.EventLocationType> eventLocationTypes = Convention.getInstance().getEventLocationTypes(event);
		if (eventLocationTypes == null || eventLocationTypes.size() == 0) {
			return 0; // Unknown
		}
		if (eventLocationTypes.size() > 1) {
			return R.string.hybrid_event;
		} else {
			return eventLocationTypes.get(0).getDescriptionStringId();
		}
	}

	private String getTimeMessage(Context context, Date eventTime, boolean includeToday, Date excludeDayIfSame) {
		boolean isToday = Dates.isSameDate(eventTime, Dates.now());
		boolean excludeDay = excludeDayIfSame != null && Dates.isSameDate(eventTime, excludeDayIfSame);

		if (excludeDay || (isToday && !includeToday)) {
			return context.getString(R.string.changed_time, Dates.formatHoursAndMinutes(eventTime));
		} else if (isToday) {
			return context.getString(R.string.changed_time_today, Dates.formatHoursAndMinutes(eventTime));
		} else {
			return context.getString(R.string.changed_day_and_time, Dates.formatDay(eventTime), Dates.formatHoursAndMinutes(eventTime));
		}
	}

	private void notifyIfEventsUpdated(List<ConventionEvent> currentEvents, List<ConventionEvent> newEvents) {
		Log.i(TAG, "Events refresh: Checking if events are updated");
		List<String> changes = new LinkedList<>();
		Map<String, ConventionEvent> currentEventsById = new HashMap<>();
		for (ConventionEvent event : currentEvents) {
			currentEventsById.put(event.getId(), event);
		}

		Map<String, ConventionEvent> newEventsById = new HashMap<>();
		for (ConventionEvent event : newEvents) {
			newEventsById.put(event.getId(), event);
		}

		// Check if there are new events
		for (ConventionEvent event : newEvents) {
			if (!currentEventsById.containsKey(event.getId())) {
				changes.add("New event: " + event.getTitle() + " (" + event.getId() + ")");
			}
		}

		// Check if any events were deleted
		for (ConventionEvent event : currentEvents) {
			if (!newEventsById.containsKey(event.getId())) {
				changes.add("Deleted event: " + event.getTitle() + " (" + event.getId() + ")");
			}
		}

		// Check for changed events
		for (ConventionEvent newEvent : newEvents) {
			ConventionEvent currentEvent = currentEventsById.get(newEvent.getId());
			if (currentEvent == null) {
				continue;
			}
			if (!newEvent.same(currentEvent)) {
				changes.add("Changed event: " + newEvent.getTitle() + " (" + newEvent.getId() + ")" +
						(newEvent.getTitle().equals(currentEvent.getTitle()) ? "" : ", previous name: " + currentEvent.getTitle()));
			}
		}

		for (String change : changes) {
			Log.i(TAG, "Events refresh: " + change);
		}
		if (changes.size() == 0) {
			Log.i(TAG, "Events refresh: No changes");
		}
	}
}
