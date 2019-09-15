package amai.org.conventions.events.activities;

import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

import amai.org.conventions.events.adapters.SectionedRecyclerViewAdapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Copied with changes from https://gist.github.com/gabrielemariotti/e81e126227f8a4bb339c
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 *
 * Use as the adapter of a sectioned recycler view by wrapping the inner adapter.
 */
public class SectionedGridRecyclerViewAdapterWrapper<T, S,
            IVH extends RecyclerView.ViewHolder, SVH extends RecyclerView.ViewHolder,
            A extends SectionedRecyclerViewAdapter<T, S, IVH, SVH>>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECTION_TYPE = 0;

    private boolean mValid = true;
    private A mBaseAdapter;
    private SparseArray<Section<S>> mSections = new SparseArray<>();


    public SectionedGridRecyclerViewAdapterWrapper(RecyclerView recyclerView, A baseAdapter) {
        mBaseAdapter = baseAdapter;
        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });

        final GridLayoutManager layoutManager = (GridLayoutManager)(recyclerView.getLayoutManager());
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (isSectionHeaderPosition(position))? layoutManager.getSpanCount() : 1 ;
            }
        });

        SectionedGridRecyclerViewAdapterWrapper.Section<S>[] sectionArray = new SectionedGridRecyclerViewAdapterWrapper.Section[mBaseAdapter.getSections().size()];
        this.setSections(mBaseAdapter.getSections().toArray(sectionArray));
    }

    public A getBaseAdapter() {
        return mBaseAdapter;
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public SectionViewHolder(View view, int mTextResourceid, int titleColor) {
            super(view);
            title = (TextView) view.findViewById(mTextResourceid);
            // ** Change from the original sample - set defined text color if requested **
            if (titleColor != Color.TRANSPARENT) {
                title.setTextColor(titleColor);
            }
        }
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            return mBaseAdapter.onCreateSectionViewHolder(parent, typeView);
        }else{
            return mBaseAdapter.onCreateViewHolder(parent, typeView -1);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            mBaseAdapter.onBindSectionViewHolder((SVH) sectionViewHolder, mSections.get(position).section);
        }else{
            mBaseAdapter.onBindViewHolder((IVH) sectionViewHolder, sectionedPositionToPosition(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) +1 ;
    }


    public static class Section<S> {
        int firstPosition;
        int sectionedPosition;
        S section;

        public Section(int firstPosition, S section) {
            this.firstPosition = firstPosition;
            this.section = section;
        }
    }


    public void setSections(Section<S>[] sections) {
        mSections.clear();

        Arrays.sort(sections, (o, o1) -> (o.firstPosition == o1.firstPosition)
                ? 0
                : ((o.firstPosition < o1.firstPosition) ? -1 : 1));

        int offset = 0; // offset positions for the headers we're adding
        for (Section<S> section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }


    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }

}
