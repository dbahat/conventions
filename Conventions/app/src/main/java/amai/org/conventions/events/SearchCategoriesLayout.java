package amai.org.conventions.events;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.EventType;

/**
 * Layout which expands all the values defined in {@link EventType} as views of type {@link SearchCategoryBox}.
 */
public class SearchCategoriesLayout extends LinearLayout {

    private OnFilterSelectedListener onFilterSelectedListener;

    public SearchCategoriesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        for (EventType eventType : EventType.values()) {
            SearchCategoryBox searchCategoryBox = createAndInitializeSearchCategoryBox(eventType);
            addView(searchCategoryBox);
        }
    }

    private SearchCategoryBox createAndInitializeSearchCategoryBox(EventType eventType) {
        final SearchCategoryBox searchCategoryBox = new SearchCategoryBox(getContext(), null);
        searchCategoryBox.setEventType(eventType);

        int padding = getResources().getDimensionPixelSize(R.dimen.search_category_padding);
        searchCategoryBox.setPadding(padding, 0, padding, 0);

        int margin = getResources().getDimensionPixelSize(R.dimen.search_category_margin);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, 0, margin, 0);
        searchCategoryBox.setLayoutParams(layoutParams);

        searchCategoryBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCategoryBox.toggle();
                if (onFilterSelectedListener != null) {
                    onFilterSelectedListener.onFilterSelected(getSelectedEventTypes());
                }
            }
        });

        return searchCategoryBox;
    }

    private List<EventType> getSelectedEventTypes() {

        List<EventType> selectedEventTypes = new LinkedList<>();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof SearchCategoryBox) {
                SearchCategoryBox searchCategoryBox = (SearchCategoryBox) child;
                if (searchCategoryBox.isChecked()) {
                    selectedEventTypes.add(searchCategoryBox.getEventType());
                }
            }
        }

        return selectedEventTypes;
    }

    public void setOnFilterSelectedListener(OnFilterSelectedListener onFilterSelectedListener) {
        this.onFilterSelectedListener = onFilterSelectedListener;
    }

    public void toggleEventType(EventType eventType) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof SearchCategoryBox) {
                SearchCategoryBox searchCategoryBox = (SearchCategoryBox) child;
                if (searchCategoryBox.getEventType() == eventType) {
                    searchCategoryBox.toggle();
                }
            }
        }
    }

    public interface OnFilterSelectedListener {
        void onFilterSelected(List<EventType> selectedEventTypes);
    }
}
