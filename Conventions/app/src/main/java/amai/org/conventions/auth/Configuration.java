/*
 * Copyright 2016 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package amai.org.conventions.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.NoClientAuthentication;

import java.lang.ref.WeakReference;

import amai.org.conventions.model.conventions.Convention;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import sff.org.conventions.BuildConfig;

/**
 * Reads and validates the demo app configuration from `res/raw/auth_config.json`. Configuration
 * changes are detected by comparing the hash of the last known configuration to the read
 * configuration. When a configuration change is detected, the app state is reset.
 */
public final class Configuration {
    private static final String PREFS_NAME = "config";

    private static WeakReference<Configuration> sInstance = new WeakReference<>(null);

    private final Context mContext;

    private String mClientId;
    private String mClientSecret;
    private String mScope;
    private Uri mRedirectUri;
    private Uri mEndSessionRedirectUri;
    private Uri mAuthEndpointUri;
    private Uri mTokenEndpointUri;
    private Uri mEndSessionEndpoint;
    private Uri mUserInfoEndpointUri;

    public static Configuration getInstance(Context context) {
        Configuration config = sInstance.get();
        if (config == null) {
            config = Convention.getInstance().getAuthConfiguration(context);
            sInstance = new WeakReference<>(config);
        }

        return config;
    }

    public Configuration(
            Context context,
            String clientId,
            String clientSecret,
            String authEndpoint,
            String tokenEndpoint,
            String endSessionEndpoint,
            String userInfoEndpoint
    ) {
        mContext = context;
        initConfiguration(clientId, clientSecret, authEndpoint, tokenEndpoint, endSessionEndpoint, userInfoEndpoint);
    }

    @Nullable
    public String getClientId() {
        return mClientId;
    }

    @Nullable
    public String getClientSecret() {
        return mClientSecret;
    }

    @Nullable
    public ClientAuthentication getClientAuthentication() {
        if (getClientSecret() == null) {
            return NoClientAuthentication.INSTANCE;
        }
        return new ClientSecretBasic(getClientSecret());
    }

    public String getScope() {
        return mScope;
    }

    public Uri getRedirectUri() {
        return mRedirectUri;
    }

    public Uri getEndSessionRedirectUri() { return mEndSessionRedirectUri; }

    public Uri getAuthEndpointUri() {
        return mAuthEndpointUri;
    }

    public Uri getTokenEndpointUri() {
        return mTokenEndpointUri;
    }

    public Uri getEndSessionEndpoint() {
        return mEndSessionEndpoint;
    }

    public Uri getUserInfoEndpointUri() {
        return mUserInfoEndpointUri;
    }

    private void initConfiguration(
            String clientId,
            String clientSecret,
            String authEndpoint,
            String tokenEndpoint,
            String endSessionEndpoint,
            String userInfoEndpoint
    ) {
        mScope = "openid email profile"; // Needed for the ID token
        mRedirectUri = getConfigUri("sff.org.conventions://oauth2redirect");
        mEndSessionRedirectUri = mRedirectUri;

        mClientId = clientId;
        mClientSecret = clientSecret;
        mAuthEndpointUri = getConfigWebUri(authEndpoint);
        mTokenEndpointUri = getConfigWebUri(tokenEndpoint);
        mEndSessionEndpoint = getConfigUri(endSessionEndpoint);
        mUserInfoEndpointUri = getConfigWebUri(userInfoEndpoint);

        if (BuildConfig.DEBUG && !isRedirectUriRegistered()) {
            throw new InvalidConfigurationException(
                    "redirect_uri is not handled by any activity in this app! "
                            + "Ensure that the appAuthRedirectScheme in your build.gradle file "
                            + "is correctly configured, or that an appropriate intent filter "
                            + "exists in your app manifest.");
        }
    }

    @NonNull
    Uri getConfigUri(String uriStr)
            throws InvalidConfigurationException {
        Uri uri;
        try {
            uri = Uri.parse(uriStr);
        } catch (Throwable ex) {
            throw new InvalidConfigurationException(uriStr + " could not be parsed", ex);
        }

        if (!uri.isHierarchical() || !uri.isAbsolute()) {
            throw new InvalidConfigurationException(
                    uriStr + " must be hierarchical and absolute");
        }

        if (!TextUtils.isEmpty(uri.getEncodedUserInfo())) {
            throw new InvalidConfigurationException(uriStr + " must not have user info");
        }

        if (!TextUtils.isEmpty(uri.getEncodedQuery())) {
            throw new InvalidConfigurationException(uriStr + " must not have query parameters");
        }

        if (!TextUtils.isEmpty(uri.getEncodedFragment())) {
            throw new InvalidConfigurationException(uriStr + " must not have a fragment");
        }

        return uri;
    }

    Uri getConfigWebUri(String uriStr)
            throws InvalidConfigurationException {
        Uri uri = getConfigUri(uriStr);
        String scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme) || !("http".equals(scheme) || "https".equals(scheme))) {
            throw new InvalidConfigurationException(
                    uriStr + " must have an http or https scheme");
        }

        return uri;
    }

    private boolean isRedirectUriRegistered() {
        // ensure that the redirect URI declared in the configuration is handled by some activity
        // in the app, by querying the package manager speculatively
        Intent redirectIntent = new Intent();
        redirectIntent.setPackage(mContext.getPackageName());
        redirectIntent.setAction(Intent.ACTION_VIEW);
        redirectIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        redirectIntent.setData(mRedirectUri);

        return !mContext.getPackageManager().queryIntentActivities(redirectIntent, 0).isEmpty();
    }

    public static final class InvalidConfigurationException extends RuntimeException {
        InvalidConfigurationException(String reason) {
            super(reason);
        }

        InvalidConfigurationException(String reason, Throwable cause) {
            super(reason, cause);
        }
    }
}
