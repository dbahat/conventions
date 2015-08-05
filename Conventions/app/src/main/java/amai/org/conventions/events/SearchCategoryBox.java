package amai.org.conventions.events;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CheckedTextView;

import amai.org.conventions.R;
import amai.org.conventions.model.EventType;

public class SearchCategoryBox extends CheckedTextView {

    private EventType eventType;

    public SearchCategoryBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEventType(EventType eventType) {
        setText(eventType.getDescription());

        Drawable drawable = getResources().getDrawable(android.R.drawable.checkbox_off_background);
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(eventType.getBackgroundColor(), PorterDuff.Mode.MULTIPLY);
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
            setBackgroundColor(getResources().getColor(R.color.dark_gray));
        } else {
            setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }
}
