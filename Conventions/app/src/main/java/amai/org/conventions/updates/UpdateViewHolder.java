package amai.org.conventions.updates;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.model.Dates;
import amai.org.conventions.model.Update;

public class UpdateViewHolder extends RecyclerView.ViewHolder {
    private TextView updateTextView;
    private TextView updateTime;
    private TextView updateDay;
    private TextView showDetailsButton;
    private LinearLayout updateTimeContainer;

    public UpdateViewHolder(View itemView) {
        super(itemView);

        updateTextView = (TextView) itemView.findViewById(R.id.update_text);
        updateTime = (TextView) itemView.findViewById(R.id.update_time);
        updateDay = (TextView) itemView.findViewById(R.id.update_day);
        showDetailsButton = (TextView) itemView.findViewById(R.id.update_show_details_button);
        updateTimeContainer = (LinearLayout) itemView.findViewById(R.id.update_time_container);
    }

    public void setContent(Update update) {
        // Wait until the updateTimeContainer layout completes before writing the text, since we want to indent
        // the next based on the measured width of the time container
        updateTimeContainer.addOnLayoutChangeListener(new SetUpdateTextTopRowsMarginOnLayoutChange(update.getText()));

        updateTime.setText(Dates.formatHoursAndMinutes(update.getDate()));
        updateDay.setText(Dates.formatDateWithoutTime(update.getDate()));
        showDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTextView.setMaxLines(Integer.MAX_VALUE);
                showDetailsButton.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Writes the update text after the time time container layout completes.
     * Done to allow indenting the update text to wrap around the time container.
     */
    private class SetUpdateTextTopRowsMarginOnLayoutChange implements View.OnLayoutChangeListener {

        // Currently hardcoding the number of lines to indent. This should be good enough since the text sizes are constant.
        private static final int NUMBER_OF_LINES_TO_INDENT = 3;

        private int width;
        private String updateText;

        public SetUpdateTextTopRowsMarginOnLayoutChange(String updateText) {
            this.updateText = updateText;
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

            if (updateTimeContainer.getWidth() > width) {
                SpannableString spannedUpdateText = new SpannableString(updateText);
                width = updateTimeContainer.getWidth();
                int extra_margin_for_top_lines = updateTextView.getResources().getDimensionPixelOffset(R.dimen.update_extra_text_margin_for_top_update_lines);
                spannedUpdateText.setSpan(
                        new WrappingTextLeadingMarginSpan(NUMBER_OF_LINES_TO_INDENT, width + extra_margin_for_top_lines),
                        0, updateText.length(), 0);
                updateTextView.setText(spannedUpdateText);
            }
        }
    }
}
