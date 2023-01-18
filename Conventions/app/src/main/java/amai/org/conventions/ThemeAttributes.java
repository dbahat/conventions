package amai.org.conventions;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import amai.org.conventions.model.conventions.Convention;

public class ThemeAttributes {
	public static int getInteger(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		int attrValue = typedArray.getInteger(0, 0);
		typedArray.recycle();
		return attrValue;
	}

	public static boolean getBoolean(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		boolean attrValue = typedArray.getBoolean(0, false);
		typedArray.recycle();
		return attrValue;
	}

	public static int getDimensionSize(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		int attrValue = typedArray.getDimensionPixelSize(0, 0);
		typedArray.recycle();
		return attrValue;
	}

	public static int getDimensionPixelOffset(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		int attrValue = typedArray.getDimensionPixelOffset(0, 0);
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

	public static ColorStateList getColorStateList(Context context, int attribute) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{attribute});
		ColorStateList attrValue = typedArray.getColorStateList(0);
		typedArray.recycle();
		return attrValue;
	}

	public static int getColorFromStateList(Context context, int attribute, int[] state) {
		// This works for a single color or a color state list drawable
		ColorStateList colorStateList = ThemeAttributes.getColorStateList(context, attribute);
		if (colorStateList == null){
			return Convention.NO_COLOR;
		}
		return colorStateList.getColorForState(state, Convention.NO_COLOR);
	}
}
