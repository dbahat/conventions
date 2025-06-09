package amai.org.conventions.model;

import android.content.Context;
import android.graphics.BlendMode;
import android.os.Build;

import amai.org.conventions.customviews.PaintDrawable;
import amai.org.conventions.customviews.RectPaintDrawable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

public class StandLocation implements Comparable<StandLocation> {
	private String id;
	private String sort;
	private String next;
	private float left = -1;
	private float right = -1;
	private float top = -1;
	private float bottom = -1;
	private int highlightColorResource;
	private BlendMode highlightBlendMode;

	public StandLocation(String id, String sort, String next) {
		this.id = id;
		this.sort = sort;
		this.next = next;
	}

	public StandLocation(String id, String sort, String next, float left, float right, float top, float bottom) {
		this(id, sort, next);
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	public static StandLocation fromWidths(String id, String sort, String next, float left, float width, float top, float height) {
		return new StandLocation(id, sort, next, left, left + width, top, top + height);
	}

	public String getId() {
		return id;
	}

	public String getSort() {
		return sort;
	}

	public String getNext() {
		return next;
	}

	public float getLeft() {
		return left;
	}

	public float getRight() {
		return right;
	}

	public float getTop() {
		return top;
	}

	public float getBottom() {
		return bottom;
	}

	public void setHighlightColorResource(int highlightColorResource) {
		this.highlightColorResource = highlightColorResource;
	}

	public void setHighlightBlendMode(BlendMode highlightBlendMode) {
		this.highlightBlendMode = highlightBlendMode;
	}

	@Override
	public int compareTo(StandLocation o) {
		return this.sort.compareTo(o.sort);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	public PaintDrawable getHighlightPaintDrawable(Context context) {
		return new RectPaintDrawable(ContextCompat.getColor(context, highlightColorResource), highlightBlendMode, left, top, right, bottom);
	}
}
