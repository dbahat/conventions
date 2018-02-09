package amai.org.conventions.events;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.AmaiModelConverter;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Strings;

public class EventView extends FrameLayout {

	private final ImageView favoriteIcon;
	private final View favoriteIconTouchArea;
	private final TextView hallName;
	private final TextView startTime;
	private final TextView timeBoxTo;
	private final TextView endTime;
	private final TextView eventName;
	private final TextView lecturerName;
	private final ImageView feedbackIcon;
	private final ImageView alarmIcon;
	private final ViewGroup timeLayout;
	private final ViewGroup eventContainer;
	private final View eventMainTouchArea;
	private final View bottomLayout;

	// Used for keyword highlighting - see setKeywordsHighlighting
	private final View searchDescriptionContainer;
	private final TextView searchDescription;
	private String eventDescriptionContent;

	public EventView(Context context) {
		this(context, null);
	}

	public EventView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(this.getContext()).inflate(R.layout.convention_event, this, true);

		timeLayout = (ViewGroup) this.findViewById(R.id.timeLayout);
		favoriteIcon = (ImageView) this.findViewById(R.id.eventFavoriteIcon);
		favoriteIconTouchArea = this.findViewById(R.id.eventFavoriteIconTouchArea);
		hallName = (TextView) this.findViewById(R.id.hallName);
		startTime = (TextView) this.findViewById(R.id.startTime);
		timeBoxTo = (TextView) this.findViewById(R.id.timeBoxTo);
		endTime = (TextView) this.findViewById(R.id.endTime);
		eventName = (TextView) this.findViewById(R.id.eventName);
		lecturerName = (TextView) this.findViewById(R.id.lecturerName);
		feedbackIcon = (ImageView) this.findViewById(R.id.feedback_icon);
		alarmIcon = (ImageView) this.findViewById(R.id.alarm_icon);
		eventContainer = (ViewGroup) this.findViewById(R.id.eventContainer);
		eventMainTouchArea = this.findViewById(R.id.eventMainTouchArea);
		bottomLayout = this.findViewById(R.id.bottom_layout);
		searchDescriptionContainer = this.findViewById(R.id.search_description_container);
		searchDescription = (TextView) this.findViewById(R.id.search_description);

		setConflicting(false);
	}

	public void setEvent(ConventionEvent event) {
		setEvent(event, false);
	}

	public void setEvent(ConventionEvent event, boolean conflicting) {
		setConflicting(conflicting);
		if (event != null) {
			setColorsFromEvent(event, conflicting);
			setAttending(event.isAttending());
			setHallName(event.getHall().getName());
			setStartTime(Dates.formatHoursAndMinutes(event.getStartTime()));
			setEndTime(Dates.formatHoursAndMinutes(event.getEndTime()));
			setEventTitle(event.getTitle());
			setLecturerName(event.getLecturer());
			setFeedbackIconFromEvent(event);
			setAlarmIconFromEvent(event);
			eventDescriptionContent = event.getPlainTextDescription();
		}

		searchDescription.setText("");

		// Setting the event id inside the view tag, so we can easily extract it from the view when listening to onClick events.
		eventMainTouchArea.setTag(event == null ? null : event.getId());
	}

	private void setColorsFromEvent(ConventionEvent event, boolean conflicting) {
		int eventTypeColor = event.getBackgroundColor(getContext());
		setEventTypeColor(eventTypeColor);
		setEventTimeTextColor(event.getTextColor(getContext()));
		if (!event.hasStarted()) {
			setEventNameColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeNotStartedColor), eventTypeColor);
			setEventBackgroundColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeNotStartedBackgroundColor));
		} else if (event.hasEnded()) {
			setEventNameColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeEndedColor), eventTypeColor);
			setEventBackgroundColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeEndedBackgroundColor));
		} else {
			setEventNameColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeCurrentColor), eventTypeColor);
			setEventBackgroundColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeCurrentBackgroundColor));
		}
	}

	public void setEventTypeColor(int color) {
		setLayoutColor(timeLayout, color);
	}

	public void setEventTimeTextColor(int color) {
		startTime.setTextColor(color);
		timeBoxTo.setTextColor(color);
		endTime.setTextColor(color);
	}

	public void setEventNameColor(int eventNameColor, int eventTypeColor) {
		// If no color is specified for the event name, use the same color from the event type
		if (eventNameColor == AmaiModelConverter.NO_COLOR) {
			eventNameColor = eventTypeColor;
		}
		eventName.setTextColor(eventNameColor);
	}

	public void setEventBackgroundColor(int color) {
		setLayoutColor(eventContainer, color);
	}

	private void setLayoutColor(ViewGroup layout, int color) {
		layout.setBackgroundColor(color);
	}

	public void setAttending(boolean isAttending) {
		favoriteIcon.setImageDrawable(ThemeAttributes.getDrawable(getContext(), R.attr.eventFavoriteIcon));
		if (isAttending) {
			favoriteIcon.setColorFilter(ThemeAttributes.getColor(getContext(), R.attr.eventFavoriteColor), PorterDuff.Mode.SRC_ATOP);
		} else {
			favoriteIcon.setColorFilter(ThemeAttributes.getColor(getContext(), R.attr.eventNonFavoriteColor), PorterDuff.Mode.SRC_ATOP);
		}
	}

	public void setShowHallName(boolean show) {
		hallName.setVisibility(show ? VISIBLE : GONE);
	}

	public void setShowFavoriteIcon(boolean show) {
		favoriteIcon.setVisibility(show ? VISIBLE : GONE);
	}

	public void setOnFavoritesButtonClickedListener(OnClickListener listener) {
		favoriteIconTouchArea.setOnClickListener(listener);
	}

	protected void setHallName(String name) {
		hallName.setText(name);
	}

	protected void setStartTime(String formattedStartTime) {
		startTime.setText(formattedStartTime);
	}

	public void setEndTime(String formattedEndTime) {
		endTime.setText(formattedEndTime);
	}

	protected void setEventTitle(String name) {
		eventName.setText(name);
	}

	protected void setLecturerName(String name) {
		lecturerName.setText(name);
	}

	protected void setFeedbackIcon(Drawable feedbackDrawable) {
		if (feedbackDrawable != null) {
			feedbackIcon.setVisibility(VISIBLE);
			feedbackIcon.setImageDrawable(feedbackDrawable);
		} else {
			feedbackIcon.setVisibility(GONE);
		}
	}

	protected void setAlarmIconFromEvent(ConventionEvent event) {
		if (event.getUserInput().getEventAboutToStartNotification().isEnabled() ||
				event.getUserInput().getEventFeedbackReminderNotification().isEnabled()) {
			alarmIcon.setVisibility(VISIBLE);
			alarmIcon.setColorFilter(ThemeAttributes.getColor(getContext(), R.attr.eventDetailsColor));
		} else {
			alarmIcon.setVisibility(GONE);
		}
	}

	protected void setFeedbackIconFromEvent(ConventionEvent event) {
		Drawable icon = null;
		if (event.canFillFeedback()) {
			Survey feedback = event.getUserInput().getFeedback();
			FeedbackQuestion.Smiley3PointAnswer weightedRating = feedback.getWeightedRating();
			int filterColor;
			if (weightedRating != null) {
				icon = ContextCompat.getDrawable(getContext(), weightedRating.getImageResourceId());
				filterColor = ContextCompat.getColor(getContext(), R.color.yellow);
			} else if ((!feedback.isSent()) && feedback.hasAnsweredQuestions() &&
					!Convention.getInstance().isFeedbackSendingTimeOver()) {
				icon = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_dialog_email);
				filterColor = ThemeAttributes.getColor(getContext(), R.attr.eventSendFeedbackColor);
			} else {
				icon = ContextCompat.getDrawable(getContext(), R.drawable.feedback);
				// If the user sent the feedback but it did not fill any smiley questions, there won't
				// be a weighted rating
				if (feedback.isSent()) {
					icon = ContextCompat.getDrawable(getContext(), R.drawable.feedback_sent);
					filterColor = ContextCompat.getColor(getContext(), R.color.yellow);
				} else if (event.isAttending() || feedback.hasAnsweredQuestions()) {
					filterColor = ThemeAttributes.getColor(getContext(), R.attr.eventSendFeedbackColor);
				} else {
					filterColor = ThemeAttributes.getColor(getContext(), R.attr.eventDetailsColor);
				}
			}

			if (icon != null) {
				icon = icon.mutate();
				icon.setColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
			}
		}
		setFeedbackIcon(icon);
	}

	private void setConflicting(boolean conflicting) {
	}

	public void setKeywordsHighlighting(List<String> keywords) {
		// Reset the state of the bottom layout, in case it was changed by recent call to set keyword highlighting
		bottomLayout.setVisibility(VISIBLE);
		searchDescriptionContainer.setVisibility(GONE);
		boolean isAnyDescriptionKeywordHighlighted = false;

		String filteredDescriptionText = eventDescriptionContent;

		int eventNameHighlightColor = ThemeAttributes.getColor(getContext(), R.attr.eventNameKeywordHighlightColor);
		int eventHighlightColor = ThemeAttributes.getColor(getContext(), R.attr.eventKeywordHighlightColor);

		for (String keyword : keywords) {
			if (keyword.length() > 0) {
				String lowerCaseKeyword = keyword.toLowerCase();
				tryHighlightKeywordInTextView(eventName, lowerCaseKeyword, eventNameHighlightColor);

				boolean didHighlightLectureName = tryHighlightKeywordInTextView(lecturerName, lowerCaseKeyword, eventHighlightColor);
				boolean didHighlightHallName = tryHighlightKeywordInTextView(hallName, lowerCaseKeyword, eventHighlightColor);

				// If the keyword is in the description, hide the lecturer name and hall name and show the description text next to the keyword instead
				// (assuming there are no highlighted keywords in the views we hid)
				if (!didHighlightLectureName && !didHighlightHallName) {
					if (filteredDescriptionText.toLowerCase().contains(lowerCaseKeyword)) {
						if (!isAnyDescriptionKeywordHighlighted) {
							bottomLayout.setVisibility(INVISIBLE); // This can't be GONE because it will mess up the favorite icon alignment
							searchDescriptionContainer.setVisibility(VISIBLE);
							searchDescription.setText(Strings.snipTextNearKeyword(filteredDescriptionText, lowerCaseKeyword));
							filteredDescriptionText = searchDescription.getText().toString();
							isAnyDescriptionKeywordHighlighted = true;
						}
						tryHighlightKeywordInTextView(searchDescription, lowerCaseKeyword, eventHighlightColor);
					}
				}
			}
		}
	}

	private boolean tryHighlightKeywordInTextView(TextView textView, String keyword, int color) {
		CharSequence originalText = textView.getText();

		String textToHighlight = originalText.toString().toLowerCase();
		if (!textToHighlight.contains(keyword)) {
			return false;
		}

		SpannableString highlightedText = originalText instanceof SpannableString
				? (SpannableString) originalText
				: new SpannableString(originalText);

		int currentKeywordIndex = textToHighlight.indexOf(keyword);
		while (currentKeywordIndex != -1) {
			// Highlight the keyword
			highlightedText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), currentKeywordIndex, currentKeywordIndex + keyword.length(), 0);
			highlightedText.setSpan(new ForegroundColorSpan(color), currentKeywordIndex, currentKeywordIndex + keyword.length(), 0);

			// Now move to highlight the next word
			currentKeywordIndex = textToHighlight.indexOf(keyword, currentKeywordIndex + keyword.length());
		}

		textView.setText(highlightedText);
		return true;
	}
}
