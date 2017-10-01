package amai.org.conventions.secondhand;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SecondHandItem;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Log;
import sff.org.conventions.R;

class SecondHandItemViewHolder extends RecyclerView.ViewHolder {
	private static final String TAG = SecondHandItemViewHolder.class.getCanonicalName();
	private SecondHandItem item;
	private final TextView itemIdView;
	private final ImageView itemEditButton;
	private final TextView itemNameView;
	private final TextView itemStatusView;

	public SecondHandItemViewHolder(View itemView) {
		super(itemView);
		itemIdView = itemView.findViewById(R.id.second_hand_item_id);
		itemEditButton = itemView.findViewById(R.id.second_hand_item_edit);
		itemNameView = itemView.findViewById(R.id.second_hand_item_name);
		itemStatusView = itemView.findViewById(R.id.second_hand_item_status);
	}

	public void setItem(SecondHandItem newItem, boolean isFormClosed) {
		this.item = newItem;
		itemIdView.setText(newItem.getId());
		refreshItemNameText();
		itemEditButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_edit_text_layout, null, false);
				final EditText itemDescriptionText = dialogView.findViewById(R.id.dialog_edit_text);
				itemDescriptionText.setInputType(InputType.TYPE_CLASS_TEXT |
						InputType.TYPE_TEXT_FLAG_AUTO_CORRECT |
						InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				if (item.getUserDescription() != null) {
					itemDescriptionText.setText(item.getUserDescription());
				}
				AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
						.setTitle(R.string.second_hand_item_edit)
						.setMessage(R.string.second_hand_item_edit_instructions)
						.setView(dialogView)
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								final String userDescription = itemDescriptionText.getText().toString();
								new AsyncTask<Void, Void, Exception>() {
									@Override
									protected Exception doInBackground(Void... params) {
										try {
											item.setUserDescription(userDescription);
											Convention.getInstance().getSecondHand().save();
											return null;
										} catch (Exception e) {
											return e;
										}
									}

									@Override
									protected void onPostExecute(Exception exception) {
										refreshItemNameText();
										if (exception != null) {
											// This is more likely to happen after the item description was set,
											// during serialization - there's nothing the user can do about it
											// but at least they'll see the new title while the app is running
											Log.e(TAG, "Could not update item user description", exception);
										}
									}
								}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
						})
						.setNegativeButton(R.string.cancel2, null)
						.create();
				dialog.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialogInterface) {
						itemDescriptionText.requestFocus();
						// Move caret to the end of the text
						// Since the keboard doesn't open automatically (unfortunately) there is no point
						// in auto-selecting all the text, but we can at least make it easier to edit by
						// putting the cursor at the end of the text instead of the beginning
						itemDescriptionText.setSelection(itemDescriptionText.getText().length());
					}
				});
				dialog.show();
			}
		});
		int statusColor;
		int titleColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandFormOpenColor);
		switch (newItem.getStatus()) {
			case SOLD:
				itemStatusView.setText(R.string.second_hand_item_sold);
				statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemSoldColor);
				break;
			case MISSING:
				itemStatusView.setText(R.string.second_hand_item_missing);
				statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemMissingColor);
				break;
			default:
				itemStatusView.setText(R.string.second_hand_item_not_sold);
				statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemNotSoldColor);
				break;
		}
		if (isFormClosed) {
			titleColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandFormClosedColor);
			statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemDefaultColor);;
		}
		itemNameView.setTextColor(titleColor);
		itemEditButton.setColorFilter(titleColor, PorterDuff.Mode.SRC_ATOP);
		itemStatusView.setTextColor(statusColor);
	}

	private void refreshItemNameText() {
		itemEditButton.setVisibility(View.VISIBLE);
		String itemName = item.getDescription();
		if (itemName == null || itemName.isEmpty()) {
			itemName = item.getUserDescription();
		} else {
			itemEditButton.setVisibility(View.GONE);
		}
		if (itemName == null || itemName.isEmpty()) {
			itemName = itemView.getContext().getString(R.string.second_hand_item_title, item.getNumber());
		}
		if (item.getType() != null && item.getType().length() > 0) {
			itemName += " (" + item.getType() + ")";
		}
		this.itemNameView.setText(itemName);
	}
}
