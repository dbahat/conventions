package amai.org.conventions.events;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.AmaiModelConverter;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Strings;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

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
    private final CardView timeLayout;
    private final ViewGroup eventContainer;
    private final View eventMainTouchArea;
    private final View bottomLayout;
    private TextView conflictingEventLabel;

    // Used for keyword highlighting - see setKeywordsHighlighting
    private final View searchDescriptionContainer;
    private final TextView searchDescription;
    private String eventDescriptionContent;


    public EventView(Context context) {
        this(context, null);
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int eventViewLayout = ThemeAttributes.getResourceId(context, R.attr.eventViewLayout);
        LayoutInflater.from(this.getContext()).inflate(eventViewLayout, this, true);

        timeLayout = this.findViewById(R.id.timeLayout);
        favoriteIcon = (ImageView) this.findViewById(R.id.eventFavoriteIcon);
        favoriteIconTouchArea = this.findViewById(R.id.eventFavoriteIconTouchArea);
        hallName = (TextView) this.findViewById(R.id.hallName);
        startTime = (TextView) this.findViewById(R.id.startTime);
        timeBoxTo = (TextView) this.findViewById(R.id.timeBoxTo);
        endTime = (TextView) this.findViewById(R.id.endTime);
        eventName = (TextView) this.findViewById(R.id.eventName);
        lecturerName = (TextView) this.findViewById(R.id.lecturerName);
        feedbackIcon = (ImageView) this.findViewById(R.id.feedback_icon);
        eventContainer = (ViewGroup) this.findViewById(R.id.eventContainer);
        eventMainTouchArea = this.findViewById(R.id.eventMainTouchArea);
        bottomLayout = this.findViewById(R.id.bottom_layout);
        searchDescriptionContainer = this.findViewById(R.id.search_description_container);
        searchDescription = (TextView) this.findViewById(R.id.search_description);
        conflictingEventLabel = this.findViewById(R.id.conflictingEventLabel);
    }

    public void setEvent(ConventionEvent event) {
        setEvent(event, false);
    }

    public void setEvent(ConventionEvent event, boolean conflicting) {
        if (event != null) {
            setColorsFromEvent(event);
            setAttending(event.isAttending());
            setHallName(event.getHall().getName());
            setStartTime(Dates.formatHoursAndMinutes(event.getStartTime()));
            setEndTime(Dates.formatHoursAndMinutes(event.getEndTime()));
            setEventTitle(event.getTitle());
            setLecturerName(event.getLecturer());
            setFeedbackIconFromEvent(event);
            eventDescriptionContent = event.getPlainTextDescription();
            setConflicting(conflicting);
        }

        searchDescription.setText("");

        // Setting the event id inside the view tag, so we can easily extract it from the view when listening to onClick events.
        eventMainTouchArea.setTag(event == null ? null : event.getId());
    }

    private void setConflicting(boolean enabled) {
        int color = ThemeAttributes.getColor(getContext(), R.attr.conflictingEventTextColor);

        // only altering the colors if conflicting, since setEvent already
        // set the non-conflict colors before calling this.
        if (color != Convention.NO_COLOR && enabled) {
            eventName.setTextColor(color);
            lecturerName.setTextColor(color);
            hallName.setTextColor(color);
        }
        if (conflictingEventLabel != null) {
            conflictingEventLabel.setVisibility(enabled ? VISIBLE : GONE);
        }
    }

    private @ColorInt int getColorFromTheme(ConventionEvent event, @AttrRes int attrNotStarted, @AttrRes int attrCurrent, @AttrRes int attrEnded) {
        @ColorInt int color;
        if (!event.hasStarted()) {
            color = ThemeAttributes.getColor(getContext(), attrNotStarted);
        } else if (event.hasEnded()) {
            color = ThemeAttributes.getColor(getContext(), attrEnded);
        } else {
            color = ThemeAttributes.getColor(getContext(), attrCurrent);
        }
        return color;
    }

    private int getEventNameColor(ConventionEvent event) {
        int eventNameColor = Convention.NO_COLOR;
        if (ThemeAttributes.getBoolean(getContext(), R.attr.useEventTextColorFromEventType)) {
            eventNameColor = event.getBackgroundColor();
        }
        if (eventNameColor == Convention.NO_COLOR) {
            eventNameColor = getColorFromTheme(event, R.attr.eventNotStartedTextColor, R.attr.eventCurrentTextColor, R.attr.eventEndedTextColor);
        }
        return eventNameColor;
    }

    private void setColorsFromEvent(ConventionEvent event) {
        setEventNameColor(getEventNameColor(event));
        setEventDetailsColor(getEventDetailsColor(event));

        int eventBackgroundColor = getColorFromTheme(event, R.attr.eventTypeNotStartedBackgroundColor, R.attr.eventTypeCurrentBackgroundColor, R.attr.eventTypeEndedBackgroundColor);
        setEventBackgroundColor(eventBackgroundColor);

        setEventTimeBackground(getEventTimeBackground(event));
        setEventTimeTextColor(getEventTimeTextColor(event));
    }

    public int getEventTimeTextColor(ConventionEvent event) {
        int textColor = Convention.NO_COLOR;
        if (ThemeAttributes.getBoolean(getContext(), R.attr.useEventTimeColorFromEventType)) {
            textColor = event.getTextColor();
        }

        if (textColor == Convention.NO_COLOR) {
            List<ConventionEvent.EventLocationType> eventLocationTypes = Convention.getInstance().getEventLocationTypes(event);
            if (eventLocationTypes != null && eventLocationTypes.size() > 0 && eventLocationTypes.get(0) == ConventionEvent.EventLocationType.VIRTUAL) {
                textColor = ThemeAttributes.getColor(getContext(), R.attr.eventTimeVirtualTextColor);
            } else {
                textColor = ThemeAttributes.getColor(getContext(), R.attr.eventTimeDefaultTextColor);
            }
        }

        return textColor;
    }

    private int getEventTimeBackground(ConventionEvent event) {
        int color = Convention.NO_COLOR;
        if (ThemeAttributes.getBoolean(getContext(), R.attr.useEventTimeColorFromEventType)) {
            color = event.getBackgroundColor();
        }

        if (color == Convention.NO_COLOR) {
            if (!event.hasStarted()) {
                color = ThemeAttributes.getColor(getContext(), R.attr.eventTimeNotStartedBackground);
            } else if (event.hasEnded()) {
                color = ThemeAttributes.getColor(getContext(), R.attr.eventTimeEndedBackground);
            } else {
                color = ThemeAttributes.getColor(getContext(), R.attr.eventTimeCurrentBackground);
            }
        }

        if (color == Convention.NO_COLOR) {
            List<ConventionEvent.EventLocationType> eventLocationTypes = Convention.getInstance().getEventLocationTypes(event);
            if (eventLocationTypes != null && eventLocationTypes.size() > 0 && eventLocationTypes.get(0) == ConventionEvent.EventLocationType.VIRTUAL) {
                color = ThemeAttributes.getColor(getContext(), R.attr.eventTimeVirtualBackgroundColor);
            } else {
                color = ThemeAttributes.getColor(getContext(), R.attr.eventTimeDefaultBackgroundColor);
            }
        }

        return color;
    }

    private int getEventDetailsColor(ConventionEvent event) {
        int eventDetailsColor = Convention.NO_COLOR;
        if (ThemeAttributes.getBoolean(getContext(), R.attr.useEventTextColorFromEventType)) {
            eventDetailsColor = event.getBackgroundColor();
        }
        if (eventDetailsColor == Convention.NO_COLOR) {
            eventDetailsColor = getColorFromTheme(event, R.attr.eventDetailsColorNotStarted, R.attr.eventDetailsColorCurrent, R.attr.eventDetailsColorEnded);
        }
        return eventDetailsColor;
    }

	public void setEventTimeBackground(int color) {
        timeLayout.setCardBackgroundColor(color);
	}

	public void setEventTimeTextColor(int color) {
		startTime.setTextColor(color);
		timeBoxTo.setTextColor(color);
		endTime.setTextColor(color);
	}

	public void setEventNameColor(int eventNameColor) {
		eventName.setTextColor(eventNameColor);
	}

    public void setEventDetailsColor(int eventDetailsColor) {
        hallName.setTextColor(eventDetailsColor);
        lecturerName.setTextColor(eventDetailsColor);
        searchDescription.setTextColor(eventDetailsColor);
    }

    public void setEventBackgroundColor(int color) {
        setLayoutColor(eventContainer, color);
    }

    private void setLayoutColor(ViewGroup layout, int color) {
        layout.setBackgroundColor(color);
    }

	public void setAttending(boolean isAttending) {
        Drawable currentStateDrawable = isAttending ?
            ThemeAttributes.getDrawable(getContext(), R.attr.eventFavoriteColor) :
            ThemeAttributes.getDrawable(getContext(), R.attr.eventNonFavoriteColor);

        if (currentStateDrawable instanceof ColorDrawable) {
            favoriteIcon.setImageDrawable(ThemeAttributes.getDrawable(getContext(), R.attr.eventFavoriteIcon));
            favoriteIcon.setColorFilter(((ColorDrawable) currentStateDrawable).getColor(), PorterDuff.Mode.SRC_ATOP);
        } else {
            favoriteIcon.setImageDrawable(currentStateDrawable);
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

    protected void setFeedbackIconFromEvent(ConventionEvent event) {
        Drawable icon = null;
        if (event.canFillFeedback()) {
            Survey feedback = event.getUserInput().getFeedback();
			FeedbackQuestion.DrawableAnswer weightedRating = feedback.getWeightedRating();
            int filterColor;
            if (weightedRating != null) {
                // Feedback was sent and has smiley answer
                icon = ContextCompat.getDrawable(getContext(), weightedRating.getImageResourceId());
                filterColor = ThemeAttributes.getColor(getContext(), R.attr.eventRatingColor);
            } else if (feedback.isSent()) {
                // Feedback was sent and has no smiley answer
                icon = ContextCompat.getDrawable(getContext(), R.drawable.feedback_sent);
                filterColor = ThemeAttributes.getColor(getContext(), R.attr.eventSentFeedbackColor);
            } else {
                if (feedback.hasAnsweredQuestions()) {
                    icon = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_dialog_email);
                } else {
                    icon = ContextCompat.getDrawable(getContext(), R.drawable.feedback);
                }

                if (!Convention.getInstance().isFeedbackSendingTimeOver() && (feedback.hasAnsweredQuestions() || event.isAttending())) {
                    // Feedback was filled and not sent or user attended (and feedback can still be sent)
                    filterColor = ThemeAttributes.getColor(getContext(), R.attr.eventSendFeedbackColor);
                } else {
                    // Feedback cannot be sent / user didn't attend or fill feedback
                    int feedbackColor = ThemeAttributes.getColor(getContext(), R.attr.eventFeedbackColor);
                    if (feedbackColor != AmaiModelConverter.NO_COLOR) {
                        filterColor = feedbackColor;
                    } else {
                        filterColor = getEventDetailsColor(event);
                    }
                }
            }

            if (icon != null) {
                icon = icon.mutate();
                icon.setColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
            }
        }
        setFeedbackIcon(icon);
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
                            bottomLayout.setVisibility(GONE);
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

        // Fix bottom layout visibility. In the convention_event layout, it can't be GONE because it will mess up the favorite icon alignment.
        if (bottomLayout.getVisibility() == GONE) {
            int eventViewLayout = ThemeAttributes.getResourceId(getContext(), R.attr.eventViewLayout);
            if (eventViewLayout == R.layout.convention_event) {
                bottomLayout.setVisibility(INVISIBLE);
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
