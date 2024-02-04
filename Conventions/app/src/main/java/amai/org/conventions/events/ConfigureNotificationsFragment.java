package amai.org.conventions.events;


import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.Date;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.notifications.PushNotification;
import amai.org.conventions.utils.Dates;

public class ConfigureNotificationsFragment extends DialogFragment {
	public static final int DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES = 5;
	public static final int DEFAULT_POST_EVENT_END_NOTIFICATION_MINUTES = 0;
	private static final String EventId = "EventId";
	private ConventionEvent event;

	private CheckBox beforeEventStartEnabledCheckbox;
	private CheckBox afterEventEndEnabledCheckbox;

	public ConfigureNotificationsFragment() {
		// Required empty public constructor
	}

	public static ConfigureNotificationsFragment newInstance(String eventId) {
		ConfigureNotificationsFragment fragment = new ConfigureNotificationsFragment();
		Bundle args = new Bundle();
		args.putString(EventId, eventId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			String eventId = getArguments().getString(EventId);
			if (eventId != null) {
				event = Convention.getInstance().findEventById(eventId);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_configure_notifications, container, false);

		Button dismissButton = (Button) view.findViewById(R.id.configure_notification_dismiss);
		dismissButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		beforeEventStartEnabledCheckbox = (CheckBox) view.findViewById(R.id.configure_notification_before_event_start_enabled);
		afterEventEndEnabledCheckbox = (CheckBox) view.findViewById(R.id.configure_notification_after_event_end_enabled);

		EventNotification eventAboutToStartNotification = event.getUserInput().getEventAboutToStartNotification();
		EventNotification eventEndedNotification = event.getUserInput().getEventFeedbackReminderNotification();

		configureCheckboxOnClickListener(beforeEventStartEnabledCheckbox, eventAboutToStartNotification);
		configureCheckboxOnClickListener(afterEventEndEnabledCheckbox, eventEndedNotification);

		return view;
	}

	private void configureCheckboxOnClickListener(CheckBox checkBox, final EventNotification eventNotification) {
		checkBox.setChecked(eventNotification.isEnabled());
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateNotificationSettings();
			}
		});
	}

	private void updateNotificationSettings() {
		EventNotification feedbackReminder = event.getUserInput().getEventFeedbackReminderNotification();
		if (afterEventEndEnabledCheckbox.isChecked()) {
			feedbackReminder.setTimeDiffInMillis(DEFAULT_POST_EVENT_END_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE);
			Date afterEventNotificationTime = event.getEventFeedbackReminderNotificationTime();
			if (afterEventNotificationTime.before(new Date())) {
				Toast.makeText(getActivity(), R.string.cannot_set_past_alarm, Toast.LENGTH_SHORT).show();
				feedbackReminder.disable();
				afterEventEndEnabledCheckbox.setChecked(false);
			}
			ConventionsApplication.alarmScheduler.scheduleFillFeedbackOnEventNotification(event, afterEventNotificationTime.getTime());
		} else {
			feedbackReminder.disable();
		}
		if (!feedbackReminder.isEnabled()) {
			ConventionsApplication.alarmScheduler.cancelEventAlarm(event, PushNotification.Type.EventFeedbackReminder);
		}

		EventNotification eventStartNotification = event.getUserInput().getEventAboutToStartNotification();
		if (beforeEventStartEnabledCheckbox.isChecked()) {
			eventStartNotification.setTimeDiffInMillis(- DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES * Dates.MILLISECONDS_IN_MINUTE);
			Date beforeEventNotificationTime = event.getEventAboutToStartNotificationTime();
			if (beforeEventNotificationTime.before(new Date())) {
				Toast.makeText(getActivity(), R.string.cannot_set_past_alarm, Toast.LENGTH_SHORT).show();
				eventStartNotification.disable();
				beforeEventStartEnabledCheckbox.setChecked(false);
			}
			ConventionsApplication.alarmScheduler.scheduleEventAboutToStartNotification(event, beforeEventNotificationTime.getTime());
		} else {
			eventStartNotification.disable();
		}
		if (!eventStartNotification.isEnabled()) {
			ConventionsApplication.alarmScheduler.cancelEventAlarm(event, PushNotification.Type.EventAboutToStart);
		}

		Convention.getInstance().getStorage().saveUserInput();

	}
}
