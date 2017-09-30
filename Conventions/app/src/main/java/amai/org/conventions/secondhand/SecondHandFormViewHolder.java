package amai.org.conventions.secondhand;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SecondHandForm;
import sff.org.conventions.R;

class SecondHandFormViewHolder extends RecyclerView.ViewHolder {
	private SecondHandForm form;
	private FormEventListener formDeletedListener;
	private final TextView formName;
	private final TextView formStatus;

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
	}

	public void setForm(SecondHandForm form) {
		this.form = form;
		formName.setText(itemView.getContext().getString(R.string.second_hand_form_name, form.getId()));
		if (form.isClosed()) {
			formStatus.setVisibility(View.VISIBLE);
			formStatus.setText(R.string.second_hand_form_closed);
			int color = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandFormClosedColor);
			formName.setTextColor(color);
			formStatus.setTextColor(color);
		} else {
			formStatus.setVisibility(View.INVISIBLE); // This must be invisible and not gone to keep the layout correct
			formName.setTextColor(ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandFormOpenColor));
		}
	}

	public void setFormDeletedListener(FormEventListener listener) {
		this.formDeletedListener = listener;
	}

	public interface FormEventListener {
		void onEvent(SecondHandForm form);
	}
}
