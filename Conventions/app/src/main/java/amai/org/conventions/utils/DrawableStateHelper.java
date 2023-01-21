package amai.org.conventions.utils;

import java.util.Arrays;

public class DrawableStateHelper {
	private int[] state;

	public void setState(int[] newState, Runnable refreshDrawableState) {
		if (!Arrays.equals(state, newState)) {
			state = newState;
			refreshDrawableState.run();
		}
	}

	public int[] onCreateDrawableState(int extraSpace, OnCreateDrawableStateCallable parentOnCreateDrawableState, MergeDrawableStatesCallable mergeDrawableStates) {
		if (state != null && state.length > 0) {
			final int[] drawableState = parentOnCreateDrawableState.run(extraSpace + state.length);
			mergeDrawableStates.run(drawableState, state);
			return drawableState;
		}
		return parentOnCreateDrawableState.run(extraSpace);
	}

	public interface OnCreateDrawableStateCallable {
		int[] run(int extraSpace);
	}

	public interface MergeDrawableStatesCallable {
		int[] run(int[] state, int[] other);
	}
}
