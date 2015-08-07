package amai.org.conventions.events;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.text.Html;
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
    private final ViewGroup timeLayout;
    private final ViewGroup eventDescription;
    private final CardView eventContainer;
    private final View bottomLayout;
    private final TextView searchDescription;

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

        // Keep the description text without any HTML markup, for usage inside setKeywordsHighlighting()
        searchDescription.setText(Html.fromHtml(event.getDescription()).toString().replace("\n", " "));

        // Setting the event id inside the view tag, so we can easily extract it from the view when listening to onClick events.
        eventContainer.setTag(event.getId());
    }

    private void setColorsFromEvent(ConventionEvent event) {
        setEventTypeColor(event.getBackgroundColor(getContext()));
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
        Resources resources = getContext().getResources();
        int favorite_icon = isAttending ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off;
        faveIcon.setImageDrawable(resources.getDrawable(favorite_icon));
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
            lecturerName.setVisibility(GONE);
            feedbackIcon.setVisibility(VISIBLE);
            feedbackIcon.setImageDrawable(feedbackDrawable);
        } else {
            lecturerName.setVisibility(VISIBLE);
            feedbackIcon.setVisibility(GONE);
        }
    }

    protected void setFeedbackIconFromEvent(ConventionEvent event) {
        Drawable icon = null;
        if (event.canFillFeedback()) {
            FeedbackQuestion.Smiley3PointAnswer weightedRating = event.getUserInput().getFeedback().getWeightedRating();
            int filterColor;
            if (weightedRating != null) {
                icon = getResources().getDrawable(weightedRating.getImageResourceId());
                filterColor = getResources().getColor(R.color.yellow);
            } else if (event.getUserInput().getFeedback().hasAnsweredQuestions()) {
                icon = getResources().getDrawable(android.R.drawable.ic_dialog_email);
                filterColor = getResources().getColor(R.color.green);
            } else {
                icon = getResources().getDrawable(R.drawable.feedback);
                // If the user sent the feedback but it did not fill any smiley questions, there won't
                // be a weighted rating
                if (event.getUserInput().getFeedback().isSent()) {
                    icon = getResources().getDrawable(R.drawable.feedback_sent);
                    filterColor = getResources().getColor(R.color.yellow);
                } else if (event.isAttending()) {
                    filterColor = getResources().getColor(R.color.green);
                } else {
                    filterColor = getResources().getColor(R.color.very_dark_gray);
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
        boolean wasDescriptionSnipped = false;

        for (String keyword : keywords) {
            if (keyword.length() > 0) {
                String lowerCaseKeyword = keyword.toLowerCase();
                tryHighlightKeywordInTextView(eventName, lowerCaseKeyword);

                boolean didHighlightLectureName = tryHighlightKeywordInTextView(lecturerName, lowerCaseKeyword);
                boolean didHighlightHallName = tryHighlightKeywordInTextView(hallName, lowerCaseKeyword);

                String filteredDescriptionText = searchDescription.getText().toString().toLowerCase();
                boolean isKeywordInDescription = filteredDescriptionText.contains(lowerCaseKeyword);

                // If the keyword is in the description, hide the lecturer name and hall name and show the description text next to the keyword insead
                // (assuming there are no highlighted keywords in the views we hid)
                if (!didHighlightLectureName && !didHighlightHallName && isKeywordInDescription) {
                    bottomLayout.setVisibility(GONE);
                    searchDescription.setVisibility(VISIBLE);

                    if (!wasDescriptionSnipped) {
                        searchDescription.setText(Strings.snipTextNearKeyword(filteredDescriptionText, lowerCaseKeyword));
                        wasDescriptionSnipped = true;
                    }
                    tryHighlightKeywordInTextView(searchDescription, lowerCaseKeyword);
                }
            }
        }
    }

    private boolean tryHighlightKeywordInTextView(TextView textView, String keyword) {
        CharSequence originalText = textView.getText();

        String textToHighlight = originalText.toString();
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
