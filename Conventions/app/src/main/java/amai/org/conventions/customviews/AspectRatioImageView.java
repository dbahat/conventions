package amai.org.conventions.customviews;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AspectRatioImageView extends ImageView {
    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Point point = AspectRatioMeasurer.measure(getLayoutParams().width, getLayoutParams().height,
                getDrawable(), getMeasuredWidth(), getMeasuredHeight());
        if (point != null) {
            setMeasuredDimension(point.x, point.y);
        }
    }
}
