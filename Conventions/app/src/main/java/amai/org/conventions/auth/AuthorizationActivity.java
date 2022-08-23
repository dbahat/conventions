package amai.org.conventions.auth;

import android.content.Intent;
import android.os.Bundle;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.EndSessionRequest;
import net.openid.appauth.EndSessionResponse;
import net.openid.appauth.IdToken;
import net.openid.appauth.ResponseTypeValues;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.utils.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import sff.org.conventions.R;

public class AuthorizationActivity extends AppCompatActivity {
	private static final String TAG = AuthorizationActivity.class.getCanonicalName();

	public final static String PARAM_SIGN_OUT = "SignOut";

	private final static String EXTRA_ACCESS_TOKEN = "AccessToken";
	private final static String EXTRA_EMAIL = "Email";
	public final static String EXTRA_USER_CANCELLED = "UserCancelled";

	private final static int TYPE_CUSTOM_ERROR = 100;

	private AuthorizationService mAuthService;
	private AuthStateManager mAuthStateManager;
	private Configuration mConfiguration;
	private ExecutorService mExecutor;
	private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleAuthResult);
	private final ActivityResultLauncher<Intent> logoutLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleLogoutResult);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mExecutor = Executors.newSingleThreadExecutor();
		mAuthStateManager = AuthStateManager.getInstance(this);
		mConfiguration = Configuration.getInstance(this);

		mExecutor.submit(this::initializeAppAuth);

		boolean signOut = getIntent().getBooleanExtra(PARAM_SIGN_OUT, false);

		if (signOut) {
			mExecutor.submit(this::doSignOut);
		} else {
			mExecutor.submit(this::doSignIn);
		}
	}

	@WorkerThread
	private void initializeAppAuth() {
		Log.i(TAG, "initializeAppAuth");
		if (mAuthService != null) {
			mAuthService.dispose();
		}
		mAuthService = new AuthorizationService(this);

		if (mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration() == null) {
			AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(
					mConfiguration.getAuthEndpointUri(),
					mConfiguration.getTokenEndpointUri(),
					null,
					mConfiguration.getEndSessionEndpoint());
			mAuthStateManager.replace(new AuthState(config));
		}
	}

	@WorkerThread
	private void doSignIn() {
		if (!mAuthStateManager.getCurrent().isAuthorized()) {
			login();
		} else {
			Log.i(TAG, "User is authenticated");
			setAuthResultAndFinish(true);
		}
	}

	private void setAuthResultAndFinish(boolean retryLoginOnError) {
		Log.i(TAG, "setAuthResultAndFinish");
		// Check the access token is fresh
		mAuthStateManager.getCurrent().performActionWithFreshTokens(mAuthService, mConfiguration.getClientAuthentication(), (accessToken, idToken, ex) -> {
			mAuthStateManager.replace(mAuthStateManager.getCurrent());
			if (ex != null) {
				Log.e(TAG, "authentication error: " + ex.error + " - " + ex.errorDescription);
				if (retryLoginOnError) {
					// Re-try the login flow
					this.login();
				} else {
					setResult(RESULT_CANCELED, ex.toIntent());
					finish();
				}
				return;
			}
			Intent intent = new Intent();
			String email = getEmail();
			intent.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
			intent.putExtra(EXTRA_EMAIL, email);
			setResult(RESULT_OK, intent);
			finish();
		});
	}

	@WorkerThread
	private AuthorizationRequest getAuthRequest() {
		AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
				mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
				mConfiguration.getClientId(),
				ResponseTypeValues.CODE,
				mConfiguration.getRedirectUri())
				.setScope(mConfiguration.getScope());
		return authRequestBuilder.build();
	}

	private CustomTabsIntent getCustomTabsIntent() {
		CustomTabColorSchemeParams.Builder tabColorBuilder = new CustomTabColorSchemeParams.Builder()
				.setToolbarColor(ThemeAttributes.getColor(this, R.attr.customTabsToolbarColor));
		CustomTabsIntent.Builder intentBuilder = mAuthService
				.createCustomTabsIntentBuilder()
				.setDefaultColorSchemeParams(tabColorBuilder.build());
		return intentBuilder.build();
	}

	@WorkerThread
	private void login() {
		Log.i(TAG, "login");
		AuthorizationRequest authRequest = getAuthRequest();
		Intent intent = mAuthService.getAuthorizationRequestIntent(authRequest, getCustomTabsIntent());
		loginLauncher.launch(intent);
	}

	private void handleAuthResult(ActivityResult result) {
		Log.i(TAG, "handleAuthResult");
		AuthorizationResponse resp = AuthorizationResponse.fromIntent(result.getData());
		AuthorizationException ex = AuthorizationException.fromIntent(result.getData());

		// User cancelled
		if (AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.equals(ex)) {
			setResult(result.getResultCode(), result.getData());
			finish();
			return;
		}

		mAuthStateManager.updateAfterAuthorization(resp, ex);

		if (ex != null) {
			setResult(result.getResultCode(), result.getData());
			finish();
		} else if (resp == null) {
			AuthorizationException missingResponse = new AuthorizationException(TYPE_CUSTOM_ERROR, 2, "missing_response", "No response or error returned from auth result", null, null);
			setResult(result.getResultCode(), missingResponse.toIntent());
			finish();
		} else {
			// Continue with the login flow
			exchangeAuthorizationCode(resp);
		}
	}

	private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
		mAuthService.performTokenRequest(
				authorizationResponse.createTokenExchangeRequest(),
				mConfiguration.getClientAuthentication(),
				(tokenResponse, authException) -> {
					mAuthStateManager.updateAfterTokenResponse(tokenResponse, authException);

					if (!mAuthStateManager.getCurrent().isAuthorized()) {
						// Error - authorization code exchange failed
						AuthorizationException ex = authException;
						if (ex == null) {
							ex = new AuthorizationException(
								TYPE_CUSTOM_ERROR, 1, "code_exchange_failed", "No access token found after authorization code exchange", null, null);
						}
						setResult(RESULT_CANCELED, ex.toIntent());
						finish();
					} else {
						setAuthResultAndFinish(false);
					}
				});
	}

	private String getEmail() {
		IdToken idToken = mAuthStateManager.getCurrent().getParsedIdToken();
		if (idToken == null) {
			return null;
		}
		Object emailObj = idToken.additionalClaims.get("email");
		return emailObj == null ? null : emailObj.toString();
	}

	@WorkerThread
	private void doSignOut() {
		EndSessionRequest endSessionRequest =
				new EndSessionRequest.Builder(mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration())
						.setIdTokenHint(mAuthStateManager.getCurrent().getIdToken())
						.setPostLogoutRedirectUri(mConfiguration.getEndSessionRedirectUri())
						.build();
		Intent intent = mAuthService.getEndSessionRequestIntent(endSessionRequest, getCustomTabsIntent());
		logoutLauncher.launch(intent);
	}

	private void handleLogoutResult(ActivityResult result) {
		Log.i(TAG, "handleLogoutResult");
		Intent data = result.getData();
		EndSessionResponse resp = EndSessionResponse.fromIntent(data);
		AuthorizationException ex = AuthorizationException.fromIntent(data);

		if (AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.equals(ex)) {
			setResult(result.getResultCode(), result.getData());
			finish();
			return;
		}

		if (ex != null) {
			mAuthStateManager.updateAfterAuthorization(null, ex);
		} else {
			mAuthStateManager.replace(new AuthState());
		}

		setResult(result.getResultCode(), data);
		finish();
	}

	public static class SignInResult {
		public boolean userCancelled;
		public Exception exception;
		public String accessToken;
		public String email;

		public static SignInResult fromActivityResult(ActivityResult activityResult) {
			SignInResult result = new SignInResult();

			int resultCode = activityResult.getResultCode();
			Intent data = activityResult.getData();
			if (data == null) {
				Log.e(TAG, "no data, assuming user cancelled");
				result.userCancelled = true;
				return result;
			}

			AuthorizationException ex = AuthorizationException.fromIntent(data);
			result.exception = ex;

			if (AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.equals(ex)) {
				Log.e(TAG, "user cancelled");
				result.userCancelled = true;
			}
			// TODO(AUTH) check other errors?

			result.accessToken = data.getStringExtra(EXTRA_ACCESS_TOKEN);
			result.email = data.getStringExtra(EXTRA_EMAIL);

			return result;
		}
	}
}
