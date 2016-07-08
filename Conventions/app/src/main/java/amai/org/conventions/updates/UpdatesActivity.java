package amai.org.conventions.updates;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Update;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.networking.UpdatesRefresher;

public class UpdatesActivity extends NavigationActivity implements SwipeRefreshLayout.OnRefreshListener {

    private CallbackManager callbackManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LoginButton loginButton;
    private RecyclerView recyclerView;
    private UpdatesAdapter updatesAdapter;
    private View loginLayout;
	private View updatesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }

        setToolbarTitle(getResources().getString(R.string.updates));
        setContentInContentContainer(R.layout.activity_updates, false);
        resolveUiElements();

        // Initialize the recycler view.
        updatesAdapter = new UpdatesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(updatesAdapter);

	    // Enable animations and listen to when they start and finish to ensure the recycler view
	    // has a background during the animation
	    recyclerView.setItemAnimator(new DefaultItemAnimator() {
		    @Override
		    public void onAddStarting(RecyclerView.ViewHolder item) {
			    setUpdatesBackground();
			    super.onAddStarting(item);
		    }

		    @Override
		    public void onChangeStarting(RecyclerView.ViewHolder item, boolean oldItem) {
			    setUpdatesBackground();
			    super.onChangeStarting(item, oldItem);
		    }

		    @Override
		    public void onMoveStarting(RecyclerView.ViewHolder item) {
			    setUpdatesBackground();
			    super.onMoveStarting(item);
		    }

		    @Override
		    public void onRemoveStarting(RecyclerView.ViewHolder item) {
			    setUpdatesBackground();
			    super.onRemoveStarting(item);
		    }

		    @Override
		    public void onAnimationFinished(RecyclerView.ViewHolder viewHolder) {
			    super.onAnimationFinished(viewHolder);
			    removeUpdatesBackground();
		    }
	    });

        // Initialize the updates list based on the model cache.
        List<Update> updates = Convention.getInstance().getUpdates();
        initializeUpdatesList(updates);
	    showUpdates();

	    initializeFacebookLoginButton();

	    loginToFacebookAndRetrieveUpdates(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
	    if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
	    }

        // In case the user canceled his login attempt and he doesn't have any cache, show him the login button
        if (resultCode == Activity.RESULT_CANCELED && updatesAdapter.getItemCount() == 0) {
	        showLogin();
        }
    }

    @Override
    public void onRefresh() {
	    ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
			    .setCategory("PullToRefresh")
			    .setAction("RefreshUpdates")
			    .build());

	    Convention.getInstance().clearNewFlagFromAllUpdates();

	    setUpdatesBackground();
	    updatesAdapter.notifyItemRangeChanged(0, Convention.getInstance().getUpdates().size());

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            retrieveUpdatesListFromFacebookApi(accessToken, true);
        } else {
	        swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void loginToFacebookAndRetrieveUpdates(Bundle savedInstanceState) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            // If the user has a valid token use it to refresh his updates
            retrieveUpdatesListFromFacebookApi(accessToken, false);
        } else if (savedInstanceState == null) {
            // If the user has no valid token attempt to perform a silent login.
            //
            // Note - only attempt to do the silent login once when the activity is initially created, not when it's restored due to config changes.
            // This is to prevent opening the user multiple login dialogs, which is both bad UX and may result in NullPointerException from the facebook SDK side
            // (since the double dialogs may trigger its OnActivityResult twice)
            LoginManager.getInstance().logInWithReadPermissions(this, null);
        } else {
            // If we got here it means we both don't have the token and we already attempted to perform silent sign-in once.
            // In this case, show the login button.
	        showLogin();
        }
    }

    private void resolveUiElements() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.updates_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ThemeAttributes.getColor(this, R.attr.toolbarBackground));

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginLayout = findViewById(R.id.login_layout);
        recyclerView = (RecyclerView) findViewById(R.id.updates_list);
	    updatesLayout = findViewById(R.id.updates_layout);
    }

	private void showLogin() {
		loginLayout.setVisibility(View.VISIBLE);
		updatesLayout.setVisibility(View.GONE);
	}

	private void showUpdates() {
		loginLayout.setVisibility(View.GONE);
		updatesLayout.setVisibility(View.VISIBLE);
	}

    private void initializeFacebookLoginButton() {
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

	        @Override
	        public void onSuccess(final LoginResult loginResult) {
		        showUpdates();

		        new Handler().post(new Runnable() {
			        @Override
			        public void run() {
				        retrieveUpdatesListFromFacebookApi(loginResult.getAccessToken(), true);
			        }
		        });
	        }

	        @Override
	        public void onCancel() {
		        showLogin();
	        }

	        @Override
	        public void onError(FacebookException e) {
		        if (loginLayout.getVisibility() == View.VISIBLE) {
		            Toast.makeText(UpdatesActivity.this, R.string.update_login_error, Toast.LENGTH_SHORT).show();
		        }
		        showLogin();
	        }
        });
    }

    private void retrieveUpdatesListFromFacebookApi(final AccessToken accessToken, final boolean showError) {
	    final UpdatesRefresher refresher = UpdatesRefresher.getInstance(UpdatesActivity.this);

        // Workaround (Android issue #77712) - SwipeRefreshLayout indicator does not appear when the `setRefreshing(true)` is called before
        // the `SwipeRefreshLayout#onMeasure()`, so we post the setRefreshing call to the layout queue.
	    refresher.setIsRefreshInProgress(true);
        swipeRefreshLayout.post(new Runnable() {
	        @Override
	        public void run() {
		        swipeRefreshLayout.setRefreshing(refresher.isRefreshInProgress());
	        }
        });

        // Refresh, and don't allow new updates notification to occur due to this refresh
	    refresher.refreshFromServer(accessToken, false, new UpdatesRefresher.OnUpdateFinishedListener() {
		    @Override
		    public void onSuccess(int newUpdatesNumber) {
			    updateRefreshingFlag();
			    initializeUpdatesList(Convention.getInstance().getUpdates());
			    // If we don't do that, the recycler view will show the previous items and the user will have to scroll manually
			    recyclerView.scrollToPosition(0);
		    }

		    @Override
		    public void onError(FacebookRequestError error) {
			    updateRefreshingFlag();
			    if (showError) {
			        Toast.makeText(UpdatesActivity.this, R.string.update_refresh_failed, Toast.LENGTH_LONG).show();
			    }
		    }

		    @Override
		    public void onInvalidTokenError() {
			    updateRefreshingFlag();
			    if (showError) {
			        Toast.makeText(UpdatesActivity.this, R.string.update_refresh_failed, Toast.LENGTH_LONG).show();
			    }
		    }

		    private void updateRefreshingFlag() {
			    swipeRefreshLayout.setRefreshing(false);
		    }
	    });
    }

    private void initializeUpdatesList(List<Update> updates) {
        Collections.sort(updates, new Comparator<Update>() {
	        @Override
	        public int compare(Update lhs, Update rhs) {
		        // Sort the updates so the latest message would appear first.
		        return rhs.getDate().compareTo(lhs.getDate());
	        }
        });

	    setUpdatesBackground();
        updatesAdapter.setUpdates(updates);
    }

	/**
	 * This method must be called before recycler view animation starts and adapter updates to prevent flickering
	 */
	private void setUpdatesBackground() {
		updatesLayout.setBackgroundColor(ThemeAttributes.getColor(this, R.attr.updatesBackground));
	}

	/**
	 * This method must be called after recycler view animation ends to remove overdraw
	 */
	private void removeUpdatesBackground() {
		// Ensure we only remove the background after the last animation finished running
		recyclerView.getItemAnimator().isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
			@Override
			public void onAnimationsFinished() {
				updatesLayout.setBackground(null);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Remove new flag for viewed updates
		Convention.getInstance().clearNewFlagFromAllUpdates();
	}
}
