package amai.org.conventions.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import amai.org.conventions.utils.DrawableStateHelper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class TextViewWithState extends AppCompatTextView implements ViewWithDrawableState {
	private final DrawableStateHelper stateHelper = new DrawableStateHelper();

	public TextViewWithState(@NonNull Context context) {
		super(context);
	}

	public TextViewWithState(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public TextViewWithState(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		// stateHelper is null when this method is called from the constructor
		return stateHelper == null ? super.onCreateDrawableState(extraSpace) : stateHelper.onCreateDrawableState(extraSpace, super::onCreateDrawableState, View::mergeDrawableStates);
	}

	@Override
	public void setState(int[] newState) {
		stateHelper.setState(newState, this::refreshDrawableState);
	}
}
