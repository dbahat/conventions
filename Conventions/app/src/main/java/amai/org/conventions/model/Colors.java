package amai.org.conventions.model;

import android.graphics.Color;

public class Colors {
    public static final int PURPLE_MEDIUM = Color.rgb(165, 159, 207);
    public static final int GRAY = Color.rgb(190, 204, 217);
    public static final int PURPLE_DARK = Color.rgb(137, 99, 215);
    public static final int RED = Color.rgb(247, 115, 113);
    public static final int PURPLE_LIGHT = Color.rgb(165, 159, 207);
    public static final int YELLOW = Color.rgb(248, 233, 174);
    public static final int GOLD = Color.rgb(250, 229, 146);

    public static int fade(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] * 0.5f;
        return Color.HSVToColor(hsv);
    }
}
