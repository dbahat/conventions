package amai.org.conventions.updates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Update;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.networking.UpdatesRefresher;
import amai.org.conventions.notifications.PushNotificationDialogPresenter;
import sff.org.conventions.R;

public class UpdatesActivity extends NavigationActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private UpdatesAdapter updatesAdapter;
	private View noUpdates;
	private View updatesLayout;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
	    setUpdatesVisibility();
	    retrieveUpdatesList(false);

		// Show push notification if it arrived
		showPushNotification(getIntent());
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// Add extras to the original intent so we will focus on the same update on screen rotation
		getIntent().putExtras(intent.getExtras());
		showPushNotification(intent);
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
        retrieveUpdatesList(true);
    }

    private void resolveUiElements() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.updates_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ThemeAttributes.getColor(this, R.attr.toolbarBackground));

	    noUpdates = findViewById(R.id.no_updates);
        recyclerView = (RecyclerView) findViewById(R.id.updates_list);
	    updatesLayout = findViewById(R.id.updates_layout);
    }

	private void setUpdatesVisibility() {
		boolean showMessage = updatesAdapter.getItemCount() == 0;
		noUpdates.setVisibility(showMessage ? View.VISIBLE : View.GONE);
		updatesLayout.setVisibility(showMessage ? View.GONE : View.VISIBLE);
	}

    private void retrieveUpdatesList(final boolean showError) {
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
	    refresher.refreshFromServer(false, new UpdatesRefresher.OnUpdateFinishedListener() {
		    @Override
		    public void onSuccess(int newUpdatesNumber) {
			    updateRefreshingFlag();
			    initializeUpdatesList(Convention.getInstance().getUpdates());
			    setUpdatesVisibility();
			    // If we don't do that, the recycler view will show the previous items and the user will have to scroll manually
			    recyclerView.scrollToPosition(0);
		    }

		    @Override
		    public void onError(Exception error) {
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

	private void showPushNotification(Intent intent) {
		String messageId = intent.getStringExtra(PushNotificationDialogPresenter.EXTRA_PUSH_NOTIFICATION_MESSAGE_ID);
		int updatePosition = UpdatesAdapter.UPDATE_NOT_FOUND;
		if (messageId != null) {
			updatePosition = updatesAdapter.focusOn(messageId);
		}

		if (updatePosition == UpdatesAdapter.UPDATE_NOT_FOUND) {
			new PushNotificationDialogPresenter().present(this, intent);
		} else {
			// Scroll to update
			recyclerView.scrollToPosition(updatePosition);
		}
	}
}
