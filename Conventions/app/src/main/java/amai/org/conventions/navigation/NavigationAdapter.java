package amai.org.conventions.navigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * An adapter that configures the viewPager to navigate based on the information inside {@link NavigationPages}
 */
public class NavigationAdapter extends FragmentStatePagerAdapter {

    private NavigationPages navigationPages;

    public NavigationAdapter(FragmentManager fm, NavigationPages navigationPages) {
        super(fm);

        this.navigationPages = navigationPages;
    }

    @Override
    public Fragment getItem(int position) {
        return navigationPages.getFragment(position);
    }

    @Override
    public int getCount() {
        return navigationPages.getCount();
    }
}