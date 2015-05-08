package amai.org.conventions.model;

import android.graphics.Color;

public class Colors {
    public static int fade(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] * 0.5f;
        return Color.HSVToColor(hsv);
    }
}
