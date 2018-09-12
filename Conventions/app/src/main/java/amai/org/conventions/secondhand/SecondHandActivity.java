package amai.org.conventions.secondhand;

import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SecondHand;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Log;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import sff.org.conventions.R;

public class SecondHandActivity extends NavigationActivity implements SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = SecondHandActivity.class.getCanonicalName();

	private SwipeRefreshLayout swipeRefreshLayout;
	private View noForms;
	private StickyListHeadersListView listView;
	private TextView soldFormsTotal;
	private SecondHandItemsAdapter adapter;
	private SecondHand secondHand;
	private boolean isRefreshing;
	private boolean isAddingItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_second_hand);
		setToolbarTitle(getString(R.string.second_hand));

		secondHand = Convention.getInstance().getSecondHand();
		swipeRefreshLayout = findViewById(R.id.second_hand_swipe_layout);
		soldFormsTotal = findViewById(R.id.second_hand_sold_forms_total);
		noForms = findViewById(R.id.second_hand_no_forms_found);
		listView = findViewById(R.id.second_hand_form_items_list);
		adapter = new SecondHandItemsAdapter(secondHand.getForms());
		listView.setAdapter(adapter);
		updateSoldForms();

		isRefreshing = false;
		isAddingItem = false;
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeColors(ThemeAttributes.getColor(this, R.attr.swipeToRefreshColor));
		swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ThemeAttributes.getColor(this, R.attr.swipeToRefreshBackgroundColor));
		// This is necessary because for some reason the swipe refresh layout here doesn't recognize that
		// the sticky headers list view can scroll up, and when scrolling up it always appears which is annoying
		swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
			@Override
			public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
				return ListViewCompat.canScrollList(listView.getWrappedList(), -1);
			}
		});

		updateListVisibility();
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				updateListVisibility();
				updateSoldForms();
			}
		});

		this.setupActionButton(R.drawable.ic_add_white, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_text_layout, null, false);
				final EditText formIdText = dialogView.findViewById(R.id.dialog_edit_text);
				formIdText.setInputType(InputType.TYPE_CLASS_NUMBER);
				AlertDialog dialog = new AlertDialog.Builder(SecondHandActivity.this)
						.setTitle(R.string.add_form)
						.setMessage(R.string.add_form_instructions)
						.setView(dialogView)
						.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								final String id = formIdText.getText().toString();
								if (!isInteger(id)) {
									Toast.makeText(SecondHandActivity.this, R.string.form_id_not_a_number, Toast.LENGTH_LONG).show();
									return;
								}
								isAddingItem = true;
								updateRefreshing();
								new AsyncTask<Void, Void, Exception>() {
									@Override
									protected Exception doInBackground(Void... params) {
										try {
											secondHand.addForm(id);
											return null;
										} catch (Exception e) {
											return e;
										}
									}

									@Override
									protected void onPostExecute(Exception exception) {
										isAddingItem = false;
										updateRefreshing();
										if (exception == null) {
											adapter.notifyDataSetChanged();
											listView.post(new Runnable() {
												@Override
												public void run() {
													listView.smoothScrollToPosition(adapter.getLastFormPosition());
												}
											});
										} else {
											int messageId = R.string.update_refresh_failed;
											if (exception instanceof SecondHand.FormNotFoundException ||
													exception instanceof SecondHand.NoItemsException) {
												messageId = R.string.form_not_found;
											} else if (exception instanceof SecondHand.FormAlreadyExists) {
												messageId = R.string.second_hand_form_already_exists;
											} else {
												Log.e(TAG, exception.getMessage(), exception);
											}
											Toast.makeText(SecondHandActivity.this, messageId, Toast.LENGTH_LONG).show();
										}
									}
								}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
						})
						.setNegativeButton(R.string.cancel, null)
						.create();
				dialog.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialogInterface) {
						formIdText.requestFocus();
					}
				});
				dialog.show();
			}
		});

		if (secondHand.shouldAutoRefresh()) {
			swipeRefreshLayout.post(new Runnable() {
				@Override
				public void run() {
					isRefreshing = true;
					updateRefreshing();
					onRefresh();
				}
			});
		}
	}

	private boolean isInteger(String string) {
		try {
			Integer.valueOf(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void updateListVisibility() {
		if (secondHand.getForms().size() == 0) {
			listView.setVisibility(View.GONE);
			noForms.setVisibility(View.VISIBLE);
		} else {
			listView.setVisibility(View.VISIBLE);
			noForms.setVisibility(View.GONE);
		}
	}

	@Override
	public void onRefresh() {
		isRefreshing = true;
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				return secondHand.refresh(true);
			}

			@Override
			protected void onPostExecute(Boolean success) {
				isRefreshing = false;
				updateRefreshing();
				adapter.setForms(secondHand.getForms());
				adapter.notifyDataSetChanged();
				if (!success) {
					Toast.makeText(SecondHandActivity.this, R.string.update_refresh_failed, Toast.LENGTH_LONG).show();
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void updateSoldForms() {
		String soldFormsMessage = secondHand.getSoldFormsMessage(SecondHandActivity.this);
		if (soldFormsMessage != null && !soldFormsMessage.isEmpty()) {
			soldFormsTotal.setVisibility(View.VISIBLE);
			soldFormsTotal.setText(soldFormsMessage);
		} else {
			soldFormsTotal.setVisibility(View.GONE);
		}
	}

	private void updateRefreshing() {
		swipeRefreshLayout.setRefreshing(isRefreshing || isAddingItem);
	}
}
