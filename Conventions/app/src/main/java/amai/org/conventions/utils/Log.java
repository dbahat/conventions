package amai.org.conventions.utils;

/**
 * Thin wrapper around the android logcat.
 * Allows disabling the logs on store build.
 *
 * Note - Currently not using ProGuard for this, since it conflict's with the App's usage of the default Java object serializer / reflection.
 */
public class Log {

    private static final boolean isEnabled = false;

    public static int v(String tag, String msg) {
        return isEnabled ? android.util.Log.v(tag, msg) : 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        return isEnabled ? android.util.Log.v(tag, msg, tr) : 0;
    }

    public static int i(String tag, String msg) {
        return isEnabled ? android.util.Log.i(tag, msg) : 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        return isEnabled ? android.util.Log.i(tag, msg, tr) : 0;
    }

    public static int w(String tag, String msg) {
        return isEnabled ? android.util.Log.w(tag, msg) : 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        return isEnabled ? android.util.Log.w(tag, msg, tr) : 0;
    }

    public static int e(String tag, String msg) {
        return isEnabled ? android.util.Log.e(tag, msg) : 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        return isEnabled ? android.util.Log.e(tag, msg, tr) : 0;
    }
}
