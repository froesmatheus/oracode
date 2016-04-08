package matheusfroes.oracode.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import matheusfroes.oracode.fragments.AboutFragment;
import matheusfroes.oracode.fragments.HistoryFragment;
import matheusfroes.oracode.fragments.SearchOracodeFragment;

/**
 * Created by mathe on 06/04/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    public static final int SEARCH_FRAGMENT_INDEX = 0;
    public static final int HISTORY_FRAGMENT_INDEX = 1;
    public static final int ABOUT_FRAGMENT_INDEX = 2;


    private SearchOracodeFragment searchOracodeFragment;
    private HistoryFragment historyFragment;
    private AboutFragment aboutFragment;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case SEARCH_FRAGMENT_INDEX:
                if (searchOracodeFragment == null) {
                    searchOracodeFragment = new SearchOracodeFragment();
                }
                return searchOracodeFragment;
            case HISTORY_FRAGMENT_INDEX:
                if (historyFragment == null) {
                    historyFragment = new HistoryFragment();
                }
                return historyFragment;
            case ABOUT_FRAGMENT_INDEX:
                if (aboutFragment == null) {
                    aboutFragment = new AboutFragment();
                }
                return aboutFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
