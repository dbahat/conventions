package amai.org.conventions.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;

import amai.org.conventions.utils.DrawableStateHelper;

/**
 * List view that supports wrap_content height inside other scroll view
 */
public class NestedListView extends ListView implements ViewWithDrawableState {
	private final DrawableStateHelper stateHelper = new DrawableStateHelper();

	public NestedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int newHeight = 0;
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		// Calculate the height even if it's "exactly" because when the view is inside a LinearLayout with
		// weight=1 we get an exact height but we don't want to take the whole height if the list itself is
		// smaller
		ListAdapter listAdapter = getAdapter();
		if (listAdapter != null && !listAdapter.isEmpty()) {
			int listPosition;
			int unspecifiedSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			for (listPosition = 0; listPosition < listAdapter.getCount(); ++listPosition) {
				View listItem = listAdapter.getView(listPosition, null, this);
				listItem.measure(widthMeasureSpec, unspecifiedSpec);
				newHeight += listItem.getMeasuredHeight();
			}
			newHeight += getDividerHeight() * listPosition;
		}
		if ((heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.EXACTLY) && (newHeight > heightSize)) {
			if (newHeight > heightSize) {
				newHeight = heightSize;
			}
		}
		setMeasuredDimension(getMeasuredWidth(), newHeight);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		// stateHelper is null when this method is called from the constructor
		return stateHelper == null ? super.onCreateDrawableState(extraSpace) : stateHelper.onCreateDrawableState(extraSpace, super::onCreateDrawableState, View::mergeDrawableStates);
	}

	public void setState(int[] newState) {
		stateHelper.setState(newState, this::refreshDrawableState);
	}
}