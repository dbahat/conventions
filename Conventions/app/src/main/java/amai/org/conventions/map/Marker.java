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
	private static final float SCALE_FACTOR = 1.3f;
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
		if (shadowImageView != null) {
			shadowWidth = shadowImageView.getLayoutParams().width;
			shadowHeight = shadowImageView.getLayoutParams().height;
		}

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

		scaleAndAnimate(imageView, getScaledSize(imageWidth), getScaledSize(imageHeight), animate);
		scaleAndAnimate(shadowImageView, getScaledSize(shadowWidth), getScaledSize(shadowHeight), animate);
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

	private int getScaledSize(int size) {
		if (size == ViewGroup.LayoutParams.WRAP_CONTENT) {
			return size;
		}
		return (int) (size * SCALE_FACTOR);
	}

	private void scaleAndAnimate(View view, int newWidth, int newHeight, boolean animate) {
		if (view == null) {
			return;
		}

		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		int width = layoutParams.width;
		int height = layoutParams.height;

		layoutParams.width = newWidth;
		layoutParams.height = newHeight;
		view.setLayoutParams(layoutParams);

		if (animate) {
			float widthScale = width / (float) newWidth;
			float heightScale = height / (float) newHeight;
			// If one of them is wrap_content, take the scale from the other size
			if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
				// Lint suppress justification: we assign the scale for the width/height, which is ok.
				//noinspection SuspiciousNameCombination
				widthScale = heightScale;
			} else if (height == ViewGroup.LayoutParams.WRAP_CONTENT) {
				//noinspection SuspiciousNameCombination
				heightScale = widthScale;
			}
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
