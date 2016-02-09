package amai.org.conventions;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

public class ThemeAttributes {
	public static int getInteger(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		int attrValue = typedArray.getInteger(0, 0);
		typedArray.recycle();
		return attrValue;
	}

	public static int getDimensionSize(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		int attrValue = typedArray.getDimensionPixelSize(0, 0);
		typedArray.recycle();
		return attrValue;
	}

	public static Drawable getDrawable(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		Drawable attrValue = typedArray.getDrawable(0);
		typedArray.recycle();
		return attrValue;
	}

	public static int getResourceId(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		int attrValue = typedArray.getResourceId(0, 0);
		typedArray.recycle();
		return attrValue;
	}

	public static int getColor(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		int attrValue = typedArray.getColor(0, 0);
		typedArray.recycle();
		return attrValue;
	}
}
