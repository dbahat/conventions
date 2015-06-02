package amai.org.conventions.map;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import amai.org.conventions.model.MapLocation;

public class Marker {
	private MapLocation location;
	private ImageView imageView;
	private View shadowImageView;
	private boolean selected = false;
	private Drawable drawable;
	private int imageWidth;
	private int imageHeight;
	private DrawableProvider selectedDrawableProvider;
	private int shadowWidth;
	private int shadowHeight;

	public Marker(MapLocation location, ImageView imageView, View shadowImageView, Drawable drawable, DrawableProvider selectedDrawableProvider) {
		this.location = location;
		this.imageView = imageView;
		this.shadowImageView = shadowImageView;
		this.drawable = drawable;
		this.selectedDrawableProvider = selectedDrawableProvider;

		imageWidth = imageView.getLayoutParams().width;
		imageHeight = imageView.getLayoutParams().height;
		shadowWidth = shadowImageView.getLayoutParams().width;
		shadowHeight = shadowImageView.getLayoutParams().height;
	}

	public MapLocation getLocation() {
		return location;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void select() {
		if (selected) {
			return;
		}
		selected = true;

		// Change the marker image and make it marker bigger. Animate the size change.
		imageView.setImageDrawable(selectedDrawableProvider.getDrawable());

		scaleAndAnimate(imageView, imageWidth + 2, imageHeight + 4);
		scaleAndAnimate(shadowImageView, shadowWidth + 2, shadowHeight + 4);
	}

	public void deselect() {
		if (!selected) {
			return;
		}
		selected = false;

		// Revert the marker image and size. Animate the size change.
		imageView.setImageDrawable(drawable);

		scaleAndAnimate(imageView, imageWidth, imageHeight);
		scaleAndAnimate(shadowImageView, shadowWidth, shadowHeight);
	}

	public boolean isSelected() {
		return selected;
	}

	private void scaleAndAnimate(View view, int newWidth, int newHeight) {
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		int width = layoutParams.width;
		int height = layoutParams.height;

		layoutParams.width = newWidth;
		layoutParams.height = newHeight;
		view.setLayoutParams(layoutParams);

		float widthScale = width / (float) newWidth;
		float heightScale = height / (float) newHeight;
		ScaleAnimation animation = new ScaleAnimation(widthScale, 1f, heightScale, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
		animation.setInterpolator(new BounceInterpolator());
		animation.setDuration(500);
		imageView.startAnimation(animation);
	}

	public interface DrawableProvider {
		Drawable getDrawable();
	}
}
