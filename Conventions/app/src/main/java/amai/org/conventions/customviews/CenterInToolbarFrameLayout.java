package amai.org.conventions.customviews;

import android.content.Context;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class CenterInToolbarFrameLayout extends FrameLayout {
	public CenterInToolbarFrameLayout(Context context) {
		super(context);
	}

	public CenterInToolbarFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CenterInToolbarFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// This custom view is used to center the title (in the child view) inside the toolbar.
		// The available width of the title changes between screens due to having a different number of menu items,
		// and if the number of items to the title's start/end isn't symmetrical, the title won't appear centered
		// even if the text view is centered inside it, so we need to add margin to its start to "balance" the space
		// taken by the action items.
		// There are cases when we can't center the title because there's not enough space for the
		// title to be in the center of the toolbar. In this case we do the next best thing and end-align it (with
		// no margin).
		// To calculate the margin we check how much room the icons on either side of the title take:
		// - To the title's start, we'll always have the navigation action as an image button.
		// - To the title's end, we'll have a changing amount of action items as an ActionMenuView.
		// We take the measured width of each of them and calculate the required margin.
		// For example:
		// If the menu has no items, we add a negative margin to the title equal to the width of the navigation item.
		// If the menu has 2 items, we add a positive margin to the title's start equal to the width of one action item.
		// The margin added at the end is half the required margin because the text is centered.

		ViewParent parent = getParent();
		if (parent instanceof Toolbar && getChildCount() == 1) {
			Toolbar toolbar = (Toolbar) parent;
			int startWidth = 0;
			int endWidth = 0;
			for (int childNumber = 0; childNumber < toolbar.getChildCount(); ++childNumber) {
				View child = toolbar.getChildAt(childNumber);
				if (child == this) {
					continue;
				}
				if (child instanceof ActionMenuView) {
					endWidth = child.getMeasuredWidth();
				} else if (child instanceof ImageButton) {
					startWidth = child.getMeasuredWidth();
				}
			}
			int startMargin = endWidth - startWidth;

			View child = getChildAt(0);
			LayoutParams childLayoutParams = (LayoutParams) child.getLayoutParams();
			childLayoutParams.setMarginStart(0); // Ensure the child width is calculated correctly
			child.setLayoutParams(childLayoutParams);

			measureChildren(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), heightMeasureSpec);
			int childWidth = child.getMeasuredWidth();

			int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
			if (childWidth + startMargin > maxWidth) {
				// Not enough room - end-align child
				childLayoutParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
				childLayoutParams.setMarginStart(0);
			} else {
				// Center-align child with margin
				childLayoutParams.gravity = Gravity.CENTER;
				childLayoutParams.setMarginStart(startMargin / 2);
			}
			child.setLayoutParams(childLayoutParams);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
