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
	private MarkerListener clickListener;

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

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (clickListener != null) {
					clickListener.onClick(Marker.this);
				}
			}
		});
	}

	public MapLocation getLocation() {
		return location;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void select() {
		select(true);
	}

	public void select(boolean animate) {
		if (selected) {
			return;
		}
		selected = true;

		// Change the marker image and make it marker bigger. Animate the size change.
		imageView.setImageDrawable(selectedDrawableProvider.getDrawable());

		scaleAndAnimate(imageView, imageWidth + 2, imageHeight + 4, animate);
		scaleAndAnimate(shadowImageView, shadowWidth + 2, shadowHeight + 4, animate);
	}

	public void deselect() {
		deselect(true);
	}

	public void deselect(boolean animate) {
		if (!selected) {
			return;
		}
		selected = false;

		// Revert the marker image and size. Animate the size change.
		imageView.setImageDrawable(drawable);

		scaleAndAnimate(imageView, imageWidth, imageHeight, animate);
		scaleAndAnimate(shadowImageView, shadowWidth, shadowHeight, animate);
	}

	public boolean isSelected() {
		return selected;
	}

	private void scaleAndAnimate(View view, int newWidth, int newHeight, boolean animate) {
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		int width = layoutParams.width;
		int height = layoutParams.height;

		layoutParams.width = newWidth;
		layoutParams.height = newHeight;
		view.setLayoutParams(layoutParams);

		if (animate) {
			float widthScale = width / (float) newWidth;
			float heightScale = height / (float) newHeight;
			ScaleAnimation animation = new ScaleAnimation(widthScale, 1f, heightScale, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
			animation.setInterpolator(new BounceInterpolator());
			animation.setDuration(500);
			imageView.startAnimation(animation);
		}
	}

	public void setOnClickListener(MarkerListener listener) {
		clickListener = listener;
	}

	public interface DrawableProvider {
		Drawable getDrawable();
	}

	public interface MarkerListener {
		void onClick(Marker marker);
	}
}
