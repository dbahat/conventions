package amai.org.conventions.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Arrays;

import sff.org.conventions.R;
import amai.org.conventions.utils.DrawableStateHelper;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

public class FrameLayoutWithState extends FrameLayout implements ViewWithDrawableState {
	private final DrawableStateHelper stateHelper = new DrawableStateHelper();

	public FrameLayoutWithState(@NonNull Context context) {
		super(context);
	}

	public FrameLayoutWithState(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public FrameLayoutWithState(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public FrameLayoutWithState(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
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
