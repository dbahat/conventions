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

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.utils.Dates;
import sff.org.conventions.R;

public class UpdateViewHolder extends RecyclerView.ViewHolder {
	private static final int MAX_LINES_FOR_COLLAPSED_UPDATE = 7;
	private static final int MAX_LINES_FOR_COLLAPSED_VIEW = 6;

	private ViewGroup updateContainer;
	private View separator;
	private TextView updateTextView;
	private TextView updateTime;
	private TextView updateDay;
	private FrameLayout showDetailsButton;
	private OnMoreInfoClickListener onMoreInfoClickListener;

	public UpdateViewHolder(View itemView) {
		super(itemView);

		updateContainer = (ViewGroup) itemView.findViewById(R.id.update_container);
		separator = itemView.findViewById(R.id.update_item_separator);
		updateTextView = (TextView) itemView.findViewById(R.id.update_text);
		updateTime = (TextView) itemView.findViewById(R.id.update_time);
		updateDay = (TextView) itemView.findViewById(R.id.update_day);
		showDetailsButton = (FrameLayout) itemView.findViewById(R.id.update_show_details_button);
	}

	public void setContent(UpdateViewModel updateViewModel) {
		SpannableString spannedUpdateText = new SpannableString(updateViewModel.getUpdate().getText());

		// Make new/focused updates highlighted
		int backgroundColor;
		int textColor;
		if (updateViewModel.isFocused()) {
			backgroundColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.focusedUpdateBackground);
			textColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.focusedUpdateTextColor);
		} else if (updateViewModel.getUpdate().isNew()) {
			backgroundColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.newUpdateBackground);
			textColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.newUpdateTextColor);
		} else {
			backgroundColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.updatesBackground);
			textColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.updateTextColor);
		}
		updateContainer.setBackgroundColor(backgroundColor);

		separator.setVisibility((getAdapterPosition() == 0) ? View.GONE : View.VISIBLE);

		updateTextView.setText(spannedUpdateText);
		updateTime.setText(Dates.formatHoursAndMinutes(updateViewModel.getUpdate().getDate()));
		updateDay.setText(Dates.formatDateWithoutTime(updateViewModel.getUpdate().getDate()));
		updateTextView.setTextColor(textColor);
		updateTime.setTextColor(textColor);
		updateDay.setTextColor(textColor);

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
