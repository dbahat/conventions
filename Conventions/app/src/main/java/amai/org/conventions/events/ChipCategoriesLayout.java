package amai.org.conventions.events;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import androidx.annotation.AttrRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.utils.CollectionUtils;

/**
 * Layout which expands all the values defined in {@link SearchCategory} as material design Chips.
 */
public class ChipCategoriesLayout extends ChipGroup {

	private OnFilterSelectedListener onFilterSelectedListener = x -> {};
	private final List<Chip> chips = new LinkedList<>();

	public ChipCategoriesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSearchCategories(List<SearchCategory> searchCategories) {
		removeAllViews();

		for (SearchCategory searchCategory : searchCategories) {
			Chip searchCategoryChip = createAndInitializeSearchCategoryChip(searchCategory);
			chips.add(searchCategoryChip);
			addView(searchCategoryChip);
		}
	}

	private Chip createAndInitializeSearchCategoryChip(SearchCategory searchCategory) {
		final Chip chip = new Chip(getContext(), null, R.style.Widget_MaterialComponents_Chip_Choice);
		chip.setCheckable(true);
		chip.setChipBackgroundColor(getThemedColor(R.attr.searchCategoriesChipBackgroundColor));
		chip.setChipStrokeColor(getThemedColor(R.attr.searchCategoriesChipStrokeColor));
		chip.setRippleColor(getThemedColor(R.attr.searchCategoriesChipRippleColor));
		chip.setTextColor(getThemedColor(R.attr.searchCategoriesChipTextColor));
		chip.setCheckedIcon(ContextCompat.getDrawable(getContext(), R.drawable.harucon2023_event_seach_chip_icon));
		chip.setChipStrokeWidth(3);
		chip.setText(searchCategory.getName());
		chip.setTag(searchCategory);

		chip.setOnClickListener(view -> {
			List<Chip> checkedChips = CollectionUtils.filter(chips, CompoundButton::isChecked);
			onFilterSelectedListener.onFilterSelected(
					CollectionUtils.map(checkedChips, item -> item.getText().toString())
			);

			// Simulate a check animation. By removing and re-adding the view, the ChipLayout animates
			// the transition of the other chips, simulating transition animation.
			// See https://stackoverflow.com/questions/71228956/animate-chip-checking-in-material-chipgroup-android
			int index = this.indexOfChild(view);
			this.removeView(view);
			this.addView(view, index);
		});

		return chip;
	}

	private ColorStateList getThemedColor(@AttrRes int attributeResourceId) {
		return ThemeAttributes.getColorStateList(getContext(), attributeResourceId);
	}

	public void setOnFilterSelectedListener(OnFilterSelectedListener onFilterSelectedListener) {
		this.onFilterSelectedListener = onFilterSelectedListener;
	}

	public void checkSearchCategory(String searchCategoryDescription) {
		Chip chip = getChipBySearchCategory(searchCategoryDescription);
		if (chip != null) {
			chip.setChecked(true);
		}
	}

	private Chip getChipBySearchCategory(final String searchCategory) {
		return CollectionUtils.findFirst(chips, chip -> chip.getText().equals(searchCategory));
	}

	public interface OnFilterSelectedListener {
		void onFilterSelected(List<String> selectedSearchCategories);
	}
}
