package amai.org.conventions.events;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventNotification;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.notifications.PushNotification;
import amai.org.conventions.utils.Dates;
import sff.org.conventions.R;

public class ConfigureNotificationsFragment extends DialogFragment {
	public static final int DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES = 10;
	public static final int DEFAULT_POST_EVENT_START_NOTIFICATION_MINUTES = 5;
	private static final String EventId = "EventId";
	private ConventionEvent event;
	private int numberOfMinutesBeforeEventNotification;
	private int numberOfMinutesAfterEventNotification;

	private Button beforeEventStartTimeButton;
	private Button afterEventEndTimeButton;
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

		beforeEventStartTimeButton = (Button) view.findViewById(R.id.configure_notification_before_event_start_time);
		afterEventEndTimeButton = (Button) view.findViewById(R.id.configure_notification_after_event_end_time);

		beforeEventStartEnabledCheckbox = (CheckBox) view.findViewById(R.id.configure_notification_before_event_start_enabled);
		afterEventEndEnabledCheckbox = (CheckBox) view.findViewById(R.id.configure_notification_after_event_end_enabled);

		EventNotification eventAboutToStartNotification = event.getUserInput().getEventAboutToStartNotification();
		EventNotification eventEndedNotification = event.getUserInput().getEventFeedbackReminderNotification();

		configureCheckboxOnClickListener(beforeEventStartEnabledCheckbox, eventAboutToStartNotification);
		configureCheckboxOnClickListener(afterEventEndEnabledCheckbox, eventEndedNotification);

		numberOfMinutesBeforeEventNotification = eventAboutToStartNotification.isEnabled()
				? (int) (event.getStartTime().getTime() - eventAboutToStartNotification.getNotificationTime().getTime()) / 1000 / 60
				: DEFAULT_PRE_EVENT_START_NOTIFICATION_MINUTES;

		numberOfMinutesAfterEventNotification = eventEndedNotification.isEnabled()
				? (int) (eventEndedNotification.getNotificationTime().getTime() - event.getEndTime().getTime()) / 1000 / 60
				: DEFAULT_POST_EVENT_START_NOTIFICATION_MINUTES;

		refreshTimeButtonsText();

		afterEventEndTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final NumberPicker numberPicker = createNumberPicker(numberOfMinutesAfterEventNotification);

				new AlertDialog.Builder(getActivity())
						.setView(numberPicker)
						.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								numberOfMinutesAfterEventNotification = numberPicker.getValue() * 5;
								afterEventEndEnabledCheckbox.setChecked(true);
								updateNotificationSettings();
								dialog.dismiss();
							}
						})
						.show();
			}
		});

		beforeEventStartTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final NumberPicker numberPicker = createNumberPicker(numberOfMinutesBeforeEventNotification);

				new AlertDialog.Builder(getActivity())
						.setView(numberPicker)
						.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								numberOfMinutesBeforeEventNotification = numberPicker.getValue() * 5;
								beforeEventStartEnabledCheckbox.setChecked(true);
								updateNotificationSettings();
								dialog.dismiss();
							}
						})
						.show();
			}
		});

		return view;
	}

	private NumberPicker createNumberPicker(int initialValue) {
		NumberPicker numberPicker = new NumberPicker(new ContextThemeWrapper(getActivity(), ThemeAttributes.getResourceId(getActivity(), R.attr.numberPickerTheme)));

		// Configure the picker values to be 0 to 59 in 5min steps
		numberPicker.setMinValue(0);
		numberPicker.setMaxValue(11);

		String[] values = new String[12];
		for (int i = 0; i < 12; i++) {
			values[i] = Integer.toString(i * 5);
		}

		numberPicker.setDisplayedValues(values);
		numberPicker.setValue(initialValue != 0 ? initialValue / 5 : 0);
		numberPicker.setWrapSelectorWheel(true);
		numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		return numberPicker;
	}

	private void refreshTimeButtonsText() {
		beforeEventStartTimeButton.setText(getString(R.string.configure_notification_time, numberOfMinutesBeforeEventNotification));
		afterEventEndTimeButton.setText(getString(R.string.configure_notification_time, numberOfMinutesAfterEventNotification));
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
			Date afterEventNotificationTime = new Date(event.getEndTime().getTime() + numberOfMinutesAfterEventNotification * Dates.MILLISECONDS_IN_MINUTE);
			if (afterEventNotificationTime.before(new Date())) {
				Toast.makeText(getActivity(), R.string.cannot_set_past_alarm, Toast.LENGTH_SHORT).show();
				feedbackReminder.setNotificationTime(null);
				afterEventEndEnabledCheckbox.setChecked(false);
			}
			feedbackReminder.setNotificationTime(afterEventNotificationTime);
			ConventionsApplication.alarmScheduler.scheduleFillFeedbackOnEventNotification(event, afterEventNotificationTime.getTime());
		} else {
			feedbackReminder.setNotificationTime(null);
		}
		if (!feedbackReminder.isEnabled()) {
			ConventionsApplication.alarmScheduler.cancelEventAlarm(event, PushNotification.Type.EventFeedbackReminder);
		}

		EventNotification eventStartNotification = event.getUserInput().getEventAboutToStartNotification();
		if (beforeEventStartEnabledCheckbox.isChecked()) {
			Date beforeEventNotificationTime = new Date(event.getStartTime().getTime() - numberOfMinutesBeforeEventNotification * Dates.MILLISECONDS_IN_MINUTE);
			if (beforeEventNotificationTime.before(new Date())) {
				Toast.makeText(getActivity(), R.string.cannot_set_past_alarm, Toast.LENGTH_SHORT).show();
				eventStartNotification.setNotificationTime(null);
				beforeEventStartEnabledCheckbox.setChecked(false);
			}
			eventStartNotification.setNotificationTime(beforeEventNotificationTime);
			ConventionsApplication.alarmScheduler.scheduleEventAboutToStartNotification(event, beforeEventNotificationTime.getTime());
		} else {
			eventStartNotification.setNotificationTime(null);
		}
		if (!eventStartNotification.isEnabled()) {
			ConventionsApplication.alarmScheduler.cancelEventAlarm(event, PushNotification.Type.EventAboutToStart);
		}

		refreshTimeButtonsText();
		Convention.getInstance().getStorage().saveUserInput();

	}
}
