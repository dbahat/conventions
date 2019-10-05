package amai.org.conventions.secondhand;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SecondHandSell;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.ListViewCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import sff.org.conventions.R;

public class SecondHandSellFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SecondHandActivity.OnFragmentSelectedListener {
	private static final String TAG = SecondHandSellFragment.class.getCanonicalName();

	private SwipeRefreshLayout swipeRefreshLayout;
	private View noForms;
	private StickyListHeadersListView listView;
	private TextView soldFormsTotal;
	private SecondHandItemsAdapter adapter;
	private SecondHandSell secondHandSell;
	private boolean isRefreshing;
	private boolean isAddingItem;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		secondHandSell = Convention.getInstance().getSecondHandSell();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_second_hand_sell, container, false);

		swipeRefreshLayout = view.findViewById(R.id.second_hand_sell_swipe_layout);
		soldFormsTotal = view.findViewById(R.id.second_hand_sold_forms_total);
		noForms = view.findViewById(R.id.second_hand_no_forms_found);
		listView = view.findViewById(R.id.second_hand_form_items_list);
		adapter = new SecondHandItemsAdapter(secondHandSell.getForms());
		listView.setAdapter(adapter);
		updateSoldForms();

		isRefreshing = false;
		isAddingItem = false;
		swipeRefreshLayout.setOnRefreshListener(this);
		Context context = getActivity();
		swipeRefreshLayout.setColorSchemeColors(ThemeAttributes.getColor(context, R.attr.swipeToRefreshColor));
		swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ThemeAttributes.getColor(context, R.attr.swipeToRefreshBackgroundColor));
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

		if (secondHandSell.shouldAutoRefresh()) {
			swipeRefreshLayout.post(new Runnable() {
				@Override
				public void run() {
					isRefreshing = true;
					updateRefreshing();
					onRefresh();
				}
			});
		}

		return view;
	}

	@Override
	public void onFragmentSelected(SecondHandActivity context) {
		context.setupActionButton(R.drawable.ic_add_white, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				View dialogView = View.inflate(builder.getContext(), R.layout.dialog_edit_text_layout, null);
				final EditText formIdText = dialogView.findViewById(R.id.dialog_edit_text);
				formIdText.setInputType(InputType.TYPE_CLASS_NUMBER);
				AlertDialog dialog = builder
						.setTitle(R.string.add_form)
						.setMessage(R.string.add_form_instructions)
						.setView(dialogView)
						.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								final String id = formIdText.getText().toString();
								if (!isInteger(id)) {
									Toast.makeText(getActivity(), R.string.form_id_not_a_number, Toast.LENGTH_LONG).show();
									return;
								}
								isAddingItem = true;
								updateRefreshing();
								new AsyncTask<Void, Void, Exception>() {
									@Override
									protected Exception doInBackground(Void... params) {
										try {
											secondHandSell.addForm(id);
											return null;
										} catch (Exception e) {
											return e;
										}
									}

									@Override
									protected void onPostExecute(Exception exception) {
										isAddingItem = false;
										updateRefreshing();

										if (getActivity() == null) {
											return;
										}
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
											if (exception instanceof SecondHandSell.FormNotFoundException ||
													exception instanceof SecondHandSell.NoItemsException) {
												messageId = R.string.form_not_found;
											} else if (exception instanceof SecondHandSell.FormAlreadyExists) {
												messageId = R.string.second_hand_form_already_exists;
											} else {
												Log.e(TAG, exception.getMessage(), exception);
											}
											Toast.makeText(getActivity(), messageId, Toast.LENGTH_LONG).show();
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
		if (secondHandSell.getForms().size() == 0) {
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
				return secondHandSell.refresh(true);
			}

			@Override
			protected void onPostExecute(Boolean success) {
				isRefreshing = false;
				updateRefreshing();

				if (getActivity() == null) {
					return;
				}
				adapter.setForms(secondHandSell.getForms());
				adapter.notifyDataSetChanged();
				if (!success) {
					Toast.makeText(getActivity(), R.string.update_refresh_failed, Toast.LENGTH_LONG).show();
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void updateSoldForms() {
		String soldFormsMessage = secondHandSell.getSoldFormsMessage(getContext());
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
