package amai.org.conventions.events;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;
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
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray params = getContext().obtainStyledAttributes(attrs, R.styleable.Event);

        setAttending(params.getBoolean(R.styleable.Event_attending, false));
        setHallName(params.getString(R.styleable.Event_hallName));
        setShowHallName(params.getBoolean(R.styleable.Event_showHallName, true));
        setShowFavoriteIcon(params.getBoolean(R.styleable.Event_showFavoriteIcon, true));
        setStartTime(params.getString(R.styleable.Event_startTime));
        setEndTime(params.getString(R.styleable.Event_endTime));
        setEventTitle(params.getString(R.styleable.Event_eventTitle));
        setLecturerName(params.getString(R.styleable.Event_lecturerName));
	    setFeedbackIcon(params.getDrawable(R.styleable.Event_feedbackIcon));

        setColorFromAttributes(timeLayout, params, R.styleable.Event_eventTypeColor);
        setColorFromAttributes(eventDescription, params, R.styleable.Event_eventColor);


        params.recycle();
    }

    public void setEvent(ConventionEvent event) {
        setColorsFromEvent(event);
        setAttending(event.isAttending());
        setHallName(event.getHall().getName());
        setStartTime(Dates.formatHoursAndMinutes(event.getStartTime()));
        setEndTime(Dates.formatHoursAndMinutes(event.getEndTime()));
        setEventTitle(event.getTitle());
        setLecturerName(event.getLecturer());
	    setFeedbackIconFromEvent(event);
	    setAlarmIconFromEvent(event);

	    eventDescriptionContent = event.getPlainTextDescription();
        searchDescription.setText("");

        // Setting the event id inside the view tag, so we can easily extract it from the view when listening to onClick events.
        eventContainer.setTag(event.getId());
    }

    private void setColorsFromEvent(ConventionEvent event) {
        setEventTypeColor(event.getBackgroundColor(getContext()));
	    setEventTimeTextColor(event.getTextColor(getContext()));
        if (!event.hasStarted()) {
            setEventColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeNotStartedColor));
        } else if (event.hasEnded()) {
            setEventColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeEndedColor));
        } else {
            setEventColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeCurrentColor));
        }
    }

    private void setColorFromAttributes(ViewGroup layout, TypedArray params, int drawableOrColorId) {
        Drawable drawable = params.getDrawable(drawableOrColorId);
        if (drawable != null) {
            setLayoutColor(layout, drawable);
        } else {
            setLayoutColor(layout, params.getColor(drawableOrColorId, Color.WHITE));
        }
    }

    public void setEventTypeColor(Drawable drawable) {
        setLayoutColor(timeLayout, drawable);
    }

    public void setEventTypeColor(int color) {
        setLayoutColor(timeLayout, color);
    }

	public void setEventTimeTextColor(int color) {
		startTime.setTextColor(color);
		endTime.setTextColor(color);
	}

    public void setEventColor(int color) {
        setLayoutColor(eventDescription, color);
    }

    public void setEventColor(Drawable drawable) {
        setLayoutColor(eventDescription, drawable);
    }

    private void setLayoutColor(ViewGroup layout, Drawable drawable) {
        setBackground(layout, drawable);
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

    private void setBackground(ViewGroup layout, Drawable drawable) {
        int pL = layout.getPaddingLeft();
        int pT = layout.getPaddingTop();
        int pR = layout.getPaddingRight();
        int pB = layout.getPaddingBottom();

        layout.setBackground(drawable);
        layout.setPadding(pL, pT, pR, pB);
    }

    public void setConflicting(boolean conflicting) {
        if (conflicting) {
            eventContainer.setCardElevation(0.0f);
            eventContainer.setMaxCardElevation(0.0f);
            eventContainer.setCardBackgroundColor(ThemeAttributes.getColor(getContext(), R.attr.conflictingEventsBackground));
        } else {
            eventContainer.setCardElevation(6.0f);
            eventContainer.setMaxCardElevation(6.0f);
            eventContainer.setCardBackgroundColor(ThemeAttributes.getColor(getContext(), R.attr.eventTypeNotStartedColor));
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
        SpannableString highlightedText = originalText instanceof SpannableString
                ? (SpannableString) originalText
                : new SpannableString(originalText);

        if (!textToHighlight.contains(keyword)) {
            return false;
        }

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
