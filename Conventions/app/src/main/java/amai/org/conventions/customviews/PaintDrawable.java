package amai.org.conventions.customviews;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface PaintDrawable {
	void onDraw(Canvas canvas, Paint paint, float widthFactor, float heightFactor);
}
