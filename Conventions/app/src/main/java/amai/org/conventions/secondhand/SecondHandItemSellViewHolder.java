package amai.org.conventions.secondhand;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SecondHandForm;
import amai.org.conventions.model.SecondHandItem;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.Strings;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

class SecondHandItemSellViewHolder extends RecyclerView.ViewHolder {
	private static final String TAG = SecondHandItemSellViewHolder.class.getCanonicalName();
	private SecondHandItem item;
	private final TextView itemIdView;
	private final TextView itemNameView;
	private final TextView itemStatusView;
	private final TextView itemPriceView;
	private final ImageView itemEditButton;

	public SecondHandItemSellViewHolder(View itemView) {
		super(itemView);
		itemIdView = itemView.findViewById(R.id.second_hand_item_id);
		itemNameView = itemView.findViewById(R.id.second_hand_item_name);
		itemStatusView = itemView.findViewById(R.id.second_hand_item_status);
		itemPriceView = itemView.findViewById(R.id.second_hand_item_price);
		itemEditButton = itemView.findViewById(R.id.second_hand_item_edit);
	}

	public void setItem(SecondHandItem newItem, SecondHandForm form) {
		this.item = newItem;
		String formId = form.getId();
		String itemId = itemView.getContext().getString(R.string.second_hand_item_id_format,
				Strings.padWithZeros(formId, 3), Strings.padWithZeros(newItem.getNumber(), 2));
		itemIdView.setText(itemId);
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
											Convention.getInstance().getSecondHandSell().save();
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
		int titleColor;
		int itemIdColor;
		if (item.getPrice() == -1) {
			itemPriceView.setVisibility(View.GONE);
		} else {
			itemPriceView.setVisibility(View.VISIBLE);
			itemPriceView.setText(itemView.getContext().getString(R.string.second_hand_item_price, item.getPrice()));
		}
		if (newItem.getStatus() == SecondHandItem.Status.UNKNOWN) {
			itemStatusView.setText(R.string.second_hand_unknown_status);
		} else {
			itemStatusView.setText(newItem.getStatusText());
		}
		if (form.isClosed()) {
			int formClosedColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandFormClosedColor);
			titleColor = formClosedColor;
			statusColor = formClosedColor;;
			itemIdColor = formClosedColor;
		} else {
			titleColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandFormOpenColor);
			itemIdColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemDefaultColor);
			switch (newItem.getStatus()) {
				case CREATED:
					statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemCreatedColor);
					break;
				case SOLD:
					statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemSoldColor);
					break;
				case MISSING:
					statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemMissingColor);
					break;
				default: // In the stand / withdrawn / donated
					statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemNotSoldColor);
					break;
			}
		}
		itemNameView.setTextColor(titleColor);
		itemEditButton.setColorFilter(titleColor, PorterDuff.Mode.SRC_ATOP);
		itemStatusView.setTextColor(statusColor);
		itemIdView.setTextColor(itemIdColor);
		itemPriceView.setTextColor(titleColor);
	}

	private void refreshItemNameText() {
		String itemName = item.getDescription();
		itemEditButton.setVisibility(View.VISIBLE);
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
