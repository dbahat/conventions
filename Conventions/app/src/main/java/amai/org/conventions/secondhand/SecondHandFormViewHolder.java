package amai.org.conventions.secondhand;

import android.view.View;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SecondHandForm;
import amai.org.conventions.utils.StateList;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

class SecondHandFormViewHolder extends RecyclerView.ViewHolder {
	private SecondHandForm form;
	private FormEventListener formDeletedListener;
	private final TextView formName;
	private final TextView formStatus;
	private final TextView formSoldItemsTotal;

	public SecondHandFormViewHolder(View itemView) {
		super(itemView);
		itemView.findViewById(R.id.second_hand_form_delete).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (formDeletedListener != null) {
					formDeletedListener.onEvent(form);
				}
			}
		});
		formName = itemView.findViewById(R.id.second_hand_form_name);
		formStatus = itemView.findViewById(R.id.second_hand_form_status);
		formSoldItemsTotal = itemView.findViewById(R.id.second_hand_form_sold_items_total);
	}

	public void setForm(SecondHandForm form) {
		this.form = form;
		StateList formState = new StateList();
		formName.setText(itemView.getContext().getString(R.string.second_hand_form_name, form.getId()));
		if (form.isClosed()) {
			formStatus.setVisibility(View.VISIBLE);
			formStatus.setText(R.string.second_hand_form_closed);
			formState.add(R.attr.state_second_hand_form_closed);
		} else {
			formStatus.setVisibility(View.INVISIBLE); // This must be invisible and not gone to keep the layout correct
		}
		int color = formState.getThemeColor(itemView.getContext(), R.attr.secondHandFormColor);
		formName.setTextColor(color);
		formStatus.setTextColor(color);
		int total = form.getSoldItemsTotalPrice();
		if (total > 0) {
			int soldItemsNumber = form.getNumberOfSoldItems();
			formSoldItemsTotal.setVisibility(View.VISIBLE);
			String soldItemsMessage;
			if (soldItemsNumber == 1) {
				soldItemsMessage = itemView.getContext().getString(R.string.second_hand_form_sold_item_total, total);
			} else {
				soldItemsMessage = itemView.getContext().getString(R.string.second_hand_form_sold_items_total, soldItemsNumber, total);
			}
			formSoldItemsTotal.setText(soldItemsMessage);
			formSoldItemsTotal.setTextColor(color);
		} else {
			formSoldItemsTotal.setVisibility(View.GONE);
		}
	}

	public void setFormDeletedListener(FormEventListener listener) {
		this.formDeletedListener = listener;
	}

	public interface FormEventListener {
		void onEvent(SecondHandForm form);
	}
}
