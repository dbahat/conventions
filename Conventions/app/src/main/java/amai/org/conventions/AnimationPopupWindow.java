package amai.org.conventions;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

public class AnimationPopupWindow extends PopupWindow {
	private int exitAnimation;

	public AnimationPopupWindow(View view, int width, int height, int enterAnimation, int exitAnimation) {
		super(view, width, height);
		this.exitAnimation = exitAnimation;
		view.setAnimation(AnimationUtils.loadAnimation(view.getContext(), enterAnimation));
	}

	@Override
	public void dismiss() {
		View view = getContentView();
		android.view.animation.Animation animation = AnimationUtils.loadAnimation(view.getContext(), exitAnimation);
		animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
			@Override
			public void onAnimationStart(android.view.animation.Animation animation) {
			}

			@Override
			public void onAnimationEnd(android.view.animation.Animation animation) {
				dismissNow();
			}

			@Override
			public void onAnimationRepeat(android.view.animation.Animation animation) {
			}
		});
		view.startAnimation(animation);
	}

	public void dismissNow() {
		super.dismiss();
	}
}
