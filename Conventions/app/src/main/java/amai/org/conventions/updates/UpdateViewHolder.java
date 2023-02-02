package amai.org.conventions.updates;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;

import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.StateList;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.utils.Dates;

public class UpdateViewHolder extends RecyclerView.ViewHolder {
	private static final int MAX_LINES_FOR_COLLAPSED_UPDATE = 5;

	private ViewGroup updateContainer;
	private View separator;
	private ImageView updateBottomLine;
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
		updateBottomLine = itemView.findViewById(R.id.update_bottom_line);
	}

	public void setContent(UpdateViewModel updateViewModel) {
		SpannableString spannedUpdateText = new SpannableString(updateViewModel.getUpdate().getText());

		StateList updateState = new StateList();
		if (updateViewModel.isFocused()) {
			updateState.add(R.attr.state_update_focused);
		}
		if (updateViewModel.getUpdate().isNew()) {
			updateState.add(R.attr.state_update_new);
		}

		updateState.setForView(updateContainer);
		updateContainer.setBackground(ThemeAttributes.getDrawable(itemView.getContext(), R.attr.updateBackground));

		separator.setVisibility((getAdapterPosition() == 0) ? View.GONE : View.VISIBLE);

		int textColor = updateState.getThemeColor(itemView.getContext(), R.attr.updateTextColor);
		updateTextView.setText(spannedUpdateText);
		updateTextView.setTextColor(textColor);

		if (updateBottomLine.getDrawable() == null) {
			updateBottomLine.setVisibility(View.GONE);
		} else {
			updateBottomLine.setVisibility(View.VISIBLE);
		}

		int updateTimeTextColor = updateState.getThemeColor(itemView.getContext(), R.attr.updateTimeColor);
		updateTime.setText(Dates.formatHoursAndMinutes(updateViewModel.getUpdate().getDate()));
		updateTime.setTextColor(updateTimeTextColor);
		updateDay.setText(Dates.formatDateWithoutTime(updateViewModel.getUpdate().getDate()));
		updateDay.setTextColor(updateTimeTextColor);

		expandViewIfNumberOfTextLinesIsSmall(updateViewModel);

		if (updateViewModel.isCollapsed()) {
			updateTextView.setMaxLines(MAX_LINES_FOR_COLLAPSED_UPDATE);
			showDetailsButton.setVisibility(View.VISIBLE);
		} else {
			updateTextView.setMaxLines(Integer.MAX_VALUE);
			showDetailsButton.setVisibility(View.GONE);
		}

		showDetailsButton.setOnClickListener(v -> {
			if (onMoreInfoClickListener != null) {
				onMoreInfoClickListener.onClicked();
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
		if (updateTextView.getLineCount() <= MAX_LINES_FOR_COLLAPSED_UPDATE) {
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
