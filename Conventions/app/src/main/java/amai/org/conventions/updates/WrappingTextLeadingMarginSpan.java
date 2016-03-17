package amai.org.conventions.updates;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * Allows to define {@link android.text.SpannableString} which wraps using pre-defined margin, only for the first X lines
 */
public class WrappingTextLeadingMarginSpan implements LeadingMarginSpan.LeadingMarginSpan2 {
    private int margin;
    private int lines;
    private boolean wasDrawCalled = false;
    private int drawLineCount = 0;

    WrappingTextLeadingMarginSpan(int lines, int margin) {
        this.margin = margin;
        this.lines = lines;
    }

    @Override
    public int getLeadingMarginLineCount() {
        return lines;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        boolean isFirstMargin = first;

        // In lollipop the meaning of the "first" parameter changed, and will now appear for the first line of all paragraphs in the text.
        // As as workaround, we keep the number of drawn lines so as not to apply the leading margin on paragraphs after the first X lines.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawLineCount = wasDrawCalled ? drawLineCount + 1 : 0;
            wasDrawCalled = false;
            isFirstMargin = drawLineCount <= lines;
        }

        return isFirstMargin ? margin : 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        wasDrawCalled = true;

        // In case the text gets re-drawn (e.g. if in a scroll/recycler view), reset its state when starting to draw again.
        if (start == 0) {
            drawLineCount = 0;
        }
    }
}
