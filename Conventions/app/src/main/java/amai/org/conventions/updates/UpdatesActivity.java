package amai.org.conventions.updates;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Dates;
import amai.org.conventions.model.Update;
import amai.org.conventions.navigation.NavigationActivity;

public class UpdatesActivity extends NavigationActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String CAMI_EVENT_FEED_PATH = "/cami.org.il/feed";
    private static final String CAMI_FACEBOOK_USERNAME = "Cami - כאמ\"י";

    private CallbackManager callbackManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LoginButton loginButton;
    private RecyclerView recyclerView;
    private UpdatesAdapter updatesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setToolbarTitle(getResources().getString(R.string.updates));
        setContentInContentContainer(R.layout.activity_updates);
        resolveUiElements();

        // Initialize the recycler view.
        updatesAdapter = new UpdatesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(updatesAdapter);

        // Initialize the updates list based on the model cache.
        initializeUpdatesList(Convention.getInstance().getUpdates());

        loginToFacebookIfNeeded();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            retrieveUpdatesListFromFacebookApi(accessToken);
        }
    }

    private void loginToFacebookIfNeeded() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            initializeFacebookLoginButton();
        } else {
            loginButton.setVisibility(View.GONE);
            retrieveUpdatesListFromFacebookApi(accessToken);
        }
    }

    private void resolveUiElements() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.updates_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ThemeAttributes.getColor(this, R.attr.toolbarBackground));

        loginButton = (LoginButton) findViewById(R.id.login_button);
        recyclerView = (RecyclerView) findViewById(R.id.updates_list);
    }

    private void initializeFacebookLoginButton() {
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {
                loginButton.setVisibility(View.GONE);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        retrieveUpdatesListFromFacebookApi(loginResult.getAccessToken());
                    }
                });
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        });
    }

    private void retrieveUpdatesListFromFacebookApi(AccessToken accessToken) {

        // Workaround (Android issue #77712) - SwipeRefreshLayout indicator does not appear when the `setRefreshing(true)` is called before
        // the `SwipeRefreshLayout#onMeasure()`, so we post the setRefreshing call to the layout queue.
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                CAMI_EVENT_FEED_PATH,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        List<Update> updates = parseAndFilterFacebookFeedResult(graphResponse);
                        // Update the model, so next time we can read them from cache.
                        Convention.getInstance().setUpdates(updates);
                        Convention.getInstance().save();
                        initializeUpdatesList(updates);

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        Bundle parameters = new Bundle();
        request.setParameters(parameters);
        request.executeAsync();
    }

    private List<Update> parseAndFilterFacebookFeedResult(GraphResponse graphResponse) {
        List<Update> updates = new LinkedList<>();
        JSONObject response = graphResponse.getJSONObject();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject post = data.getJSONObject(i);
                JSONObject from = post.getJSONObject("from");
                String fromName = from.getString("name");
                if (fromName.contains(CAMI_FACEBOOK_USERNAME) && post.has("message") && post.has("created_time") ) {
                    Update update = new Update();
                    String dateString = post.getString("created_time");
                    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Dates.getLocale()).parse(dateString);
                    update.setDate(date);

                    update.setText(post.getString("message"));
                    updates.add(update);
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        return updates;
    }

    private void initializeUpdatesList(List<Update> updates) {
        recyclerView.setVisibility(View.VISIBLE);

        Collections.sort(updates, new Comparator<Update>() {
            @Override
            public int compare(Update lhs, Update rhs) {
                // Sort the updates so the latest message would appear first.
                return rhs.getDate().compareTo(lhs.getDate());
            }
        });

        updatesAdapter.setUpdates(updates);
    }
}
