package amai.org.conventions.customviews;

import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class RectPaintDrawable implements PaintDrawable {
	private RectF box = new RectF(0, 0, 0, 0);
	private float left;
	private float top;
	private float right;
	private float bottom;
	private float rotation;
	private int color;
	private BlendMode blendMode;

	public RectPaintDrawable(int color, BlendMode blendMode, float left, float top, float right, float bottom, float rotation) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.color = color;
		this.rotation = rotation;
		this.blendMode = blendMode;
	}

	@Override
	public void onDraw(Canvas canvas, Paint paint, float widthFactor, float heightFactor) {
		paint.setColor(color);
		if (blendMode != null) {
			paint.setBlendMode(blendMode);
		}

		box.set(
			left * widthFactor,
			top  * heightFactor,
			right * widthFactor,
			bottom  * heightFactor
		);

		if (rotation != 0) {
			canvas.save();
			canvas.rotate(rotation, box.centerX(), box.centerY());
		}

		canvas.drawRect(box, paint);

		if (rotation != 0) {
			canvas.restore();
		}
	}
}
