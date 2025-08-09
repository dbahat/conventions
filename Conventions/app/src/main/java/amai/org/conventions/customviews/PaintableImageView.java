package amai.org.conventions.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.List;

public class PaintableImageView extends AspectRatioImageView {
	Paint paint = new Paint();
	List<PaintDrawable> paintDrawables;
	private float referenceWidth;
	private float referenceHeight;

	public PaintableImageView(Context context) {
		super(context);
	}

	public PaintableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PaintableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setPaintDrawables(List<PaintDrawable> paintDrawables, float referenceWidth, float referenceHeight) {
		this.paintDrawables = paintDrawables;
		this.referenceWidth = referenceWidth;
		this.referenceHeight = referenceHeight;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (paintDrawables == null || paintDrawables.isEmpty()) {
			return;
		}

		int imageWidth = getWidth();
		int imageHeight = getHeight();

		for (PaintDrawable paintDrawable : paintDrawables) {
			paintDrawable.onDraw(canvas, paint, ((float) imageWidth) / this.referenceWidth, ((float) imageHeight) / this.referenceHeight);
		}
		invalidate();
	}
}
