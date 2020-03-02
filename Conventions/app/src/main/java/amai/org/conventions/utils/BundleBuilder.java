package amai.org.conventions.utils;

import android.os.Bundle;

public class BundleBuilder {

    private final Bundle bundle;

    public BundleBuilder() {
        bundle = new Bundle();
    }

    public BundleBuilder putString(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public Bundle build() {
        return bundle;
    }
}
