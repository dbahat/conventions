package amai.org.conventions.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Allows listening to spinner selection events even if the current selection is un-changed.
 */
public class ExtendedSpinner extends Spinner {
    OnItemSelectedListener listener;

    public ExtendedSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (listener != null)
            listener.onItemSelected(null, null, position, 0);
    }

    /**
     * Listen to item selection events for all spinner item (not just the items which are not currently selected).
     * Needed in cases where the spinner allows navigation from an activity which is not in the spinner (meaning one of the spinner
     * items will become un-selectable with the default behavior).
     * @param listener listener to invoke when an item selection event occurred.
     */
    public void setOnItemSelectedEvenIfUnchangedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }
}
