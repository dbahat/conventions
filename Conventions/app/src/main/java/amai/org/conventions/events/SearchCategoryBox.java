package amai.org.conventions.events;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.EventType;
import amai.org.conventions.networking.AmaiModelParser;

public class SearchCategoryBox extends CheckedTextView {

    private EventType eventType;

    public SearchCategoryBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEventType(EventType eventType) {
        setText(eventType.getDescription());

        Drawable drawable = ContextCompat.getDrawable(getContext(), android.R.drawable.checkbox_off_background);
        if (drawable != null) {
	        int backgroundColor = eventType.getBackgroundColor();
	        if (backgroundColor == AmaiModelParser.NO_COLOR) {
		        backgroundColor = ThemeAttributes.getColor(getContext(), R.attr.eventTimeDefaultBackgroundColor);
	        }
            drawable.mutate();
            drawable.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
	        setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }

        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public void toggle() {
        super.toggle();

        if (isChecked()) {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        } else {
            setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        }
    }
}
