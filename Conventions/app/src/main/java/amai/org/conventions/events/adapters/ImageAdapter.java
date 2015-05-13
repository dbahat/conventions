package amai.org.conventions.events.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import amai.org.conventions.R;

public class ImageAdapter extends PagerAdapter {
	Context context;
	private List<Integer> images;

	public ImageAdapter(Context context, List<Integer> images) {
		this.context = context;
		this.images = images;
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(context);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setImageResource(images.get(position));
		container.addView(imageView, 0);
		return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		ImageView imageView = (ImageView) object;
		container.removeView(imageView);
	}
}