package amai.org.conventions.map;

import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.manuelpeinado.imagelayout.ImageLayout;

import amai.org.conventions.model.MapLocation;
import amai.org.conventions.utils.Views;

public class Marker {
	private static final float SCALE_FACTOR = 1.3f;
	private MapLocation location;
	private ImageView imageView;
	private boolean selected = false;
	private Drawable drawable;
	private float imageWidth;
	private float imageHeight;
	private DrawableProvider selectedDrawableProvider;
	private MarkerListener clickListener;
	private int indexInParent = -1;
	private GestureDetector detector;

	public Marker(MapLocation location, ImageView imageView, Drawable drawable, DrawableProvider selectedDrawableProvider) {
		this.location = location;
		this.imageView = imageView;
		this.drawable = drawable;
		this.selectedDrawableProvider = selectedDrawableProvider;

		ImageLayout.LayoutParams layoutParams = (ImageLayout.LayoutParams) imageView.getLayoutParams();
		imageWidth = layoutParams.width;
		imageHeight = layoutParams.height;

		imageView.setOnTouchListener(Views.createOnSingleTapConfirmedListener(imageView.getContext(), new Runnable() {
			@Override
			public void run() {
				if (clickListener != null) {
					clickListener.onClick(Marker.this);
				}
			}
		}));
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

		// Bring to front
		ViewGroup parent = (ViewGroup) imageView.getParent();
		indexInParent = parent.indexOfChild(imageView);
		parent.bringChildToFront(imageView);

		// Change the marker image and make it marker bigger. Animate the size change.
		imageView.setImageDrawable(selectedDrawableProvider.getDrawable());

		scaleAndAnimate(imageView, getScaledSize(imageWidth), getScaledSize(imageHeight), animate);
	}

	public void deselect() {
		deselect(true);
	}

	public void deselect(boolean animate) {
		if (!selected) {
			return;
		}
		selected = false;

		// Return to original z-order
		if (indexInParent != -1) {
			ViewGroup parent = (ViewGroup) imageView.getParent();
			parent.removeView(imageView);
			parent.addView(imageView, indexInParent);
			indexInParent = -1;
		}

		// Revert the marker image and size. Animate the size change.
		imageView.setImageDrawable(drawable);

		scaleAndAnimate(imageView, imageWidth, imageHeight, animate);
	}

	public boolean isSelected() {
		return selected;
	}

	private float getScaledSize(float size) {
		if (size == ViewGroup.LayoutParams.WRAP_CONTENT) {
			return size;
		}
		return size * SCALE_FACTOR;
	}

	private void scaleAndAnimate(View view, float newWidth, float newHeight, boolean animate) {
		if (view == null) {
			return;
		}

		ImageLayout.LayoutParams layoutParams = (ImageLayout.LayoutParams) view.getLayoutParams();
		float width = layoutParams.width;
		float height = layoutParams.height;

		layoutParams.width = newWidth;
		layoutParams.height = newHeight;
		view.setLayoutParams(layoutParams);

		if (animate) {
			float widthScale = width / newWidth;
			float heightScale = height / newHeight;
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
