package amai.org.conventions.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * List view that supports wrap_content height inside other scroll view
 */
public class NestedListView extends ListView {
	public NestedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int newHeight = 0;
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			ListAdapter listAdapter = getAdapter();
			if (listAdapter != null && !listAdapter.isEmpty()) {
				int listPosition = 0;
				for (listPosition = 0; listPosition < listAdapter.getCount(); ++listPosition) {
					View listItem = listAdapter.getView(listPosition, null, this);
					listItem.measure(widthMeasureSpec, heightMeasureSpec);
					newHeight += listItem.getMeasuredHeight();
				}
				newHeight += getDividerHeight() * listPosition;
			}
			if ((heightMode == MeasureSpec.AT_MOST) && (newHeight > heightSize)) {
				if (newHeight > heightSize) {
					newHeight = heightSize;
				}
			}
		} else {
			newHeight = getMeasuredHeight();
		}
		setMeasuredDimension(getMeasuredWidth(), newHeight);
	}
}