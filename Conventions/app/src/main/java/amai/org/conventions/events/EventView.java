package amai.org.conventions.events;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Strings;

public class EventView extends FrameLayout {

	private final ImageView faveIcon;
	private final TextView hallName;
	private final TextView startTime;
	private final TextView endTime;
	private final TextView eventName;
	private final TextView lecturerName;
	private final ImageView feedbackIcon;
	private final ImageView alarmIcon;
	private final ViewGroup timeLayout;
	private final ViewGroup eventDescription;
	private final CardView eventContainer;
	private final View bottomLayout;

	// Used for keyword highlighting - see setKeywordsHighlighting
	private final TextView searchDescription;
	private String eventDescriptionContent;

	public EventView(Context context) {
		this(context, null);
	}

	public EventView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(this.getContext()).inflate(R.layout.convention_event, this, true);

		timeLayout = (ViewGroup) this.findViewById(R.id.timeLayout);
		faveIcon = (ImageView) this.findViewById(R.id.faveIcon);
		hallName = (TextView) this.findViewById(R.id.hallName);
		startTime = (TextView) this.findViewById(R.id.startTime);
		endTime = (TextView) this.findViewById(R.id.endTime);
		eventName = (TextView) this.findViewById(R.id.eventName);
		lecturerName = (TextView) this.findViewById(R.id.lecturerName);
		feedbackIcon = (ImageView) this.findViewById(R.id.feedback_icon);
		alarmIcon = (ImageView) this.findViewById(R.id.alarm_icon);
		eventDescription = (ViewGroup) this.findViewById(R.id.eventDescription);
		eventContainer = (CardView) this.findViewById(R.id.eventContainer);
		bottomLayout = this.findViewById(R.id.bottom_layout);
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
		eventContainer.setTag(event == null ? null : event.getId());
	}

	private void setColorsFromEvent(ConventionEvent event, boolean conflicting) {
		setEventTypeColor(event.getBackgroundColor(getContext()));
		setEventTimeTextColor(event.getTextColor(getContext()));
		if (!event.hasStarted()) {
			setEventColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeNotStartedColor), conflicting);
		} else if (event.hasEnded()) {
			setEventColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeEndedColor), conflicting);
		} else {
			setEventColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeCurrentColor), conflicting);
		}
	}

	public void setEventTypeColor(int color) {
		setLayoutColor(timeLayout, color);
	}

	public void setEventTimeTextColor(int color) {
		startTime.setTextColor(color);
		endTime.setTextColor(color);
	}

	public void setEventColor(int color, boolean conflicting) {
		// Card implementation is different before and after Lollipop.
		// Before Lollipop the layout inside the card does not cover the entire card area.
		// Therefore if the card itself doesn't have a background color, views under it will be
		// visible and there might be artifacts in the drawing.
		// On the other hand, in order to prevent overdraw we want to draw as little layers as
		// possible on the screen.
		// The best solution we came up with is:
		// 1. If we're post-Lollipop, the card never needs a background and only the layout inside it
		//    has a color (time box and event description).
		// 2. If we're pre-Lollipop, the card must have a background. We will paint it the color of
		//    the event, and the time box will have overdraw. The event description doesn't need a
		//    background in this case.
		// 3. A special case for pre-Lollipop is when it's a conflicting event. In that case we
		//    already know what the background of the card is (a RecyclerView with a dark background)
		//    so we can have a transparent card. In that case we make the card itself transparent and
		//    only color the event description and time box.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// 1. post-Lollipop
			eventContainer.setCardBackgroundColor(Color.TRANSPARENT);
			setLayoutColor(eventDescription, color);
		} else if (conflicting) {
			// 3. Conflicting event pre-Lollipop
			// If we just color the card transparent its shadow is still visible (although the elevation
			// is 0), so we remove the background completely. Note that this relies in the inner implementation
			// of the card view! (and that due to that implementation it's impossible to return the background
			// afterwards)
			eventContainer.setBackground(null);
			setLayoutColor(eventDescription, color);
		} else {
			// 2. Non-conflicting event pre-Lollipop
			eventContainer.setCardBackgroundColor(color);
			setLayoutColor(eventDescription, Color.TRANSPARENT);
		}
	}

	private void setLayoutColor(ViewGroup layout, int color) {
		layout.setBackgroundColor(color);
	}

	public void setAttending(boolean isAttending) {
		int favorite_icon = isAttending ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off;
		faveIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), favorite_icon));
	}

	public void setShowHallName(boolean show) {
		hallName.setVisibility(show ? VISIBLE : GONE);
	}

	public void setShowFavoriteIcon(boolean show) {
		faveIcon.setVisibility(show ? VISIBLE : GONE);
	}

	public void setOnFavoritesButtonClickedListener(OnClickListener listener) {
		timeLayout.setOnClickListener(listener);
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener listener) {
		eventContainer.setOnLongClickListener(listener);
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
			alarmIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.very_dark_gray));
		} else {
			alarmIcon.setVisibility(GONE);
		}
	}

	protected void setFeedbackIconFromEvent(ConventionEvent event) {
		Drawable icon = null;
		if (event.canFillFeedback()) {
			Feedback feedback = event.getUserInput().getFeedback();
			FeedbackQuestion.Smiley3PointAnswer weightedRating = feedback.getWeightedRating();
			int filterColor;
			if (weightedRating != null) {
				icon = ContextCompat.getDrawable(getContext(), weightedRating.getImageResourceId());
				filterColor = ContextCompat.getColor(getContext(), R.color.yellow);
			} else if ((!feedback.isSent()) && feedback.hasAnsweredQuestions() &&
					!Convention.getInstance().isFeedbackSendingTimeOver()) {
				icon = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_dialog_email);
				filterColor = ContextCompat.getColor(getContext(), R.color.green);
			} else {
				icon = ContextCompat.getDrawable(getContext(), R.drawable.feedback);
				// If the user sent the feedback but it did not fill any smiley questions, there won't
				// be a weighted rating
				if (feedback.isSent()) {
					icon = ContextCompat.getDrawable(getContext(), R.drawable.feedback_sent);
					filterColor = ContextCompat.getColor(getContext(), R.color.yellow);
				} else if (event.isAttending() || feedback.hasAnsweredQuestions()) {
					filterColor = ContextCompat.getColor(getContext(), R.color.green);
				} else {
					filterColor = ContextCompat.getColor(getContext(), R.color.very_dark_gray);
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
		if (conflicting) {
			eventContainer.setCardElevation(0.0f);
			eventContainer.setMaxCardElevation(0.0f);
		} else {
			eventContainer.setCardElevation(6.0f);
			eventContainer.setMaxCardElevation(6.0f);
		}
	}

	public void setKeywordsHighlighting(List<String> keywords) {
		// Reset the state of the bottom layout, in case it was changed by recent call to set keyword highlighting
		bottomLayout.setVisibility(VISIBLE);
		searchDescription.setVisibility(GONE);
		boolean isAnyDescriptionKeywordHighlighted = false;

		String filteredDescriptionText = eventDescriptionContent;

		for (String keyword : keywords) {
			if (keyword.length() > 0) {
				String lowerCaseKeyword = keyword.toLowerCase();
				tryHighlightKeywordInTextView(eventName, lowerCaseKeyword);

				boolean didHighlightLectureName = tryHighlightKeywordInTextView(lecturerName, lowerCaseKeyword);
				boolean didHighlightHallName = tryHighlightKeywordInTextView(hallName, lowerCaseKeyword);

				// If the keyword is in the description, hide the lecturer name and hall name and show the description text next to the keyword instead
				// (assuming there are no highlighted keywords in the views we hid)
				if (!didHighlightLectureName && !didHighlightHallName) {
					if (filteredDescriptionText.toLowerCase().contains(lowerCaseKeyword)) {
						if (!isAnyDescriptionKeywordHighlighted) {
							bottomLayout.setVisibility(GONE);
							searchDescription.setVisibility(VISIBLE);
							searchDescription.setText(Strings.snipTextNearKeyword(filteredDescriptionText, lowerCaseKeyword));
							filteredDescriptionText = searchDescription.getText().toString();
							isAnyDescriptionKeywordHighlighted = true;
						}
						tryHighlightKeywordInTextView(searchDescription, lowerCaseKeyword);
					}
				}
			}
		}
	}

	private boolean tryHighlightKeywordInTextView(TextView textView, String keyword) {
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
			highlightedText.setSpan(new ForegroundColorSpan(Color.BLACK), currentKeywordIndex, currentKeywordIndex + keyword.length(), 0);

			// Now move to highlight the next word
			currentKeywordIndex = textToHighlight.indexOf(keyword, currentKeywordIndex + keyword.length());
		}

		textView.setText(highlightedText);
		return true;
	}
}
