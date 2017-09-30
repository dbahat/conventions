package amai.org.conventions.secondhand;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.model.SecondHandForm;
import amai.org.conventions.model.SecondHandItem;
import amai.org.conventions.model.conventions.Convention;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import sff.org.conventions.R;

public class SecondHandItemsAdapter extends BaseAdapter implements StickyListHeadersAdapter {
	private static final int NO_FORM = -1;
	private List<SecondHandForm> forms;
	private Map<Integer, SecondHandForm> itemIndexToForm;
	private List<SecondHandItem> items;

	public SecondHandItemsAdapter(List<SecondHandForm> forms) {
		setForms(forms);
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		final SecondHandFormViewHolder holder;
		if (convertView == null) {
			View secondHandFormView = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_hand_form, parent, false);
			holder = new SecondHandFormViewHolder(secondHandFormView);
			convertView = secondHandFormView;
			convertView.setTag(holder);
		} else {
			holder = (SecondHandFormViewHolder) convertView.getTag();
		}

		holder.setForm(itemIndexToForm.get(position));
		holder.setFormDeletedListener(new SecondHandFormViewHolder.FormEventListener() {
			@Override
			public void onEvent(final SecondHandForm form) {
				Context context = holder.itemView.getContext();
				new AlertDialog.Builder(context)
					.setTitle(R.string.second_hand_form_delete)
					.setMessage(context.getString(R.string.second_hand_form_delete_out_are_you_sure, form.getId()))
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							boolean deleted = Convention.getInstance().getSecondHand().deleteForm(form.getId());
							if (deleted) {
								notifyDataSetChanged();
							}
						}
					})
					.setNegativeButton(R.string.no, null)
					.show();
			}
		});

		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		SecondHandForm form = itemIndexToForm.get(position);
		if (form == null) {
			return NO_FORM;
		}
		int i = 0;
		for (SecondHandForm modelForm : forms) {
			if (modelForm == form) {
				return i;
			}
			++i;
		}
		return NO_FORM;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public SecondHandItem getItem(int i) {
		return items.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final SecondHandItemViewHolder holder;
		if (convertView == null) {
			View secondHandItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_hand_item, parent, false);
			holder = new SecondHandItemViewHolder(secondHandItemView);
			convertView = secondHandItemView;
			convertView.setTag(holder);
		} else {
			holder = (SecondHandItemViewHolder) convertView.getTag();
		}

		holder.setItem(items.get(position), itemIndexToForm.get(position).isClosed());
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public void setForms(List<SecondHandForm> forms) {
		this.forms = forms;
		items = new ArrayList<>();
		int i = 0;
		itemIndexToForm = new HashMap<>();
		for (SecondHandForm form : forms) {
			for (SecondHandItem item : form.getItems()) {
				items.add(item);
				itemIndexToForm.put(i, form);
				++i;
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		setForms(forms);
		super.notifyDataSetChanged();
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}
}
