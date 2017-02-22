package amai.org.conventions.updates;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.utils.Dates;

public class UpdateViewHolder extends RecyclerView.ViewHolder {
	private static final int MAX_LINES_FOR_COLLAPSED_UPDATE = 7;
	private static final int MAX_LINES_FOR_COLLAPSED_VIEW = 6;

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

		// Make new/focused updates highlighted
		int color;
		if (updateViewModel.isFocused()) {
			color = ThemeAttributes.getColor(itemView.getContext(), R.attr.focusedUpdateBackground);
		} else if (updateViewModel.getUpdate().isNew()) {
			color = ThemeAttributes.getColor(itemView.getContext(), R.attr.newUpdateBackground);
		} else {
			color = ThemeAttributes.getColor(itemView.getContext(), R.attr.updatesBackground);
		}
		updateContainer.setBackgroundColor(color);

		updateTextView.setText(spannedUpdateText);
		updateTime.setText(Dates.formatHoursAndMinutes(updateViewModel.getUpdate().getDate()));
		updateDay.setText(Dates.formatDateWithoutTime(updateViewModel.getUpdate().getDate()));

		expandViewIfNumberOfTextLinesIsSmall(updateViewModel);

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

	private void expandViewIfNumberOfTextLinesIsSmall(UpdateViewModel updateViewModel) {
		// In case the number of lines in the update is too small, show it as expanded (otherwise the user will get UX where clicking the expand button
		// results in the view getting smaller).
		Point screenSize = getScreenSize();
		// For this check we need to measure the textView size. Not using accurate measure call here due to time constraints, and since the approximate measure
		// we do here is good enough.
		updateTextView.measure(screenSize.x, screenSize.y);
		if (updateTextView.getLineCount() <= MAX_LINES_FOR_COLLAPSED_VIEW) {
			updateViewModel.setCollapsed(false);
		}
	}

	private Point getScreenSize() {
		WindowManager wm = (WindowManager) updateContainer.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}
}
