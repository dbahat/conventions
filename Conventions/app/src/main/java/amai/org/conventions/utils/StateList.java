package amai.org.conventions.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import sff.org.conventions.BuildConfig;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.ViewWithDrawableState;
import androidx.annotation.NonNull;

public class StateList {
	List<Integer> states;

	public StateList(int... initialStates) {
		states = new ArrayList<>(initialStates.length);
		for (int s : initialStates) {
			states.add(s);
		}
	}

	public StateList add(int stateAttribute) {
		states.add(stateAttribute);
		return this;
	}

	public int[] toArray() {
		int[] intStates = new int[states.size()];
		for(int i = 0; i < states.size(); ++i) {
			intStates[i] = states.get(i);
		}
		return intStates;
	}

	@NonNull
	public StateList clone() {
		return new StateList(this.toArray());
	}

	public void setForView(View view) {
		if (view instanceof ViewWithDrawableState) {
			((ViewWithDrawableState) view).setState(this.toArray());
		} else if (view instanceof ImageView) {
			((ImageView) view).setImageState(this.toArray(), false);
		} else if (BuildConfig.DEBUG) {
			throw new IllegalArgumentException("view " + view + " does not support states");
		}
	}

	public int getThemeColor(Context context, int attribute) {
		return ThemeAttributes.getColorFromStateList(context, attribute, this.toArray());
	}
}
