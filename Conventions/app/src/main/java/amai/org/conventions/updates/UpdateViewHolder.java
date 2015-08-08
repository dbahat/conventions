package amai.org.conventions.updates;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.utils.Dates;

public class UpdateViewHolder extends RecyclerView.ViewHolder {
    private static final int MAX_LINES_FOR_COLLAPSED_UPDATE = 7;

	private ViewGroup updateContainer;
    private TextView updateTextView;
    private TextView updateTime;
    private TextView updateDay;
    private FrameLayout showDetailsButton;
    private OnMoreInfoClickListener onMoreInfoClickListener;

    public UpdateViewHolder(View itemView) {
        super(itemView);

	    updateContainer = (ViewGroup) itemView.findViewById(R.id.update_container);
        updateTextView = (TextView) itemView.findViewById(R.id.update_text);
        updateTime = (TextView) itemView.findViewById(R.id.update_time);
        updateDay = (TextView) itemView.findViewById(R.id.update_day);
        showDetailsButton = (FrameLayout) itemView.findViewById(R.id.update_show_details_button);
    }

    public void setContent(UpdateViewModel updateViewModel) {
        int timeContainerSize = updateTextView.getContext().getResources().getDimensionPixelSize(R.dimen.update_time_container_size);
        SpannableString spannedUpdateText = new SpannableString(updateViewModel.getUpdate().getText());
        int extra_margin_for_top_lines = updateTextView.getResources().getDimensionPixelOffset(R.dimen.update_extra_text_margin_for_top_update_lines);
	    int textLength = updateViewModel.getUpdate().getText().length();
	    spannedUpdateText.setSpan(
		        new WrappingTextLeadingMarginSpan(4, timeContainerSize + extra_margin_for_top_lines),
		        0, textLength, 0);

	    // Make new updates highlighted
	    if (updateViewModel.getUpdate().isNew()) {
		    updateContainer.setBackgroundColor(itemView.getResources().getColor(R.color.very_light_gray));
	    } else {
		    updateContainer.setBackgroundColor(itemView.getResources().getColor(R.color.white));
	    }

        updateTextView.setText(spannedUpdateText);

        updateTime.setText(Dates.formatHoursAndMinutes(updateViewModel.getUpdate().getDate()));
        updateDay.setText(Dates.formatDateWithoutTime(updateViewModel.getUpdate().getDate()));

        if (updateViewModel.isCollapsed()) {
            updateTextView.setMaxLines(MAX_LINES_FOR_COLLAPSED_UPDATE);
            showDetailsButton.setVisibility(View.VISIBLE);
        } else {
            updateTextView.setMaxLines(Integer.MAX_VALUE);
            showDetailsButton.setVisibility(View.GONE);
        }

        showDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMoreInfoClickListener != null) {
                    onMoreInfoClickListener.onClicked();
                }
            }
        });
    }

    public void setOnMoreInfoClickListener(OnMoreInfoClickListener listener) {
        this.onMoreInfoClickListener = listener;
    }

    public interface OnMoreInfoClickListener {
        void onClicked();
    }
}
