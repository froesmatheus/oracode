package matheusfroes.oracode.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener;

import matheusfroes.oracode.R;
import matheusfroes.oracode.adapters.ViewPagerAdapter;
import matheusfroes.oracode.fragments.HistoryFragment;
import matheusfroes.oracode.fragments.SearchOracodeFragment;
import matheusfroes.oracode.utils.Utils;

public class MainActivity extends AppCompatActivity implements HistoryFragment.OnHistoryItemClick,
        SearchOracodeFragment.OnHistoryUpdate {
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigation;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        BottomNavigationItem bniSearch = new BottomNavigationItem(getString(R.string.search), ContextCompat.getColor(this, R.color.colorPrimary), R.drawable.ic_search);
        BottomNavigationItem bniHistory = new BottomNavigationItem(getString(R.string.history), ContextCompat.getColor(this, R.color.colorPrimary), R.drawable.ic_history);
        BottomNavigationItem bniAbout = new BottomNavigationItem(getString(R.string.about), ContextCompat.getColor(this, R.color.colorPrimary), R.drawable.ic_about);

        bottomNavigation.addTab(bniSearch);
        bottomNavigation.addTab(bniHistory);
        bottomNavigation.addTab(bniAbout);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigation.selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        bottomNavigation.setOnBottomNavigationItemClickListener(new OnBottomNavigationItemClickListener() {
            @Override
            public void onNavigationItemClick(int index) {
                viewPager.setCurrentItem(index, true);
            }
        });
    }


    @Override
    public void onHistoryItemClick(String oracode) {
        SearchOracodeFragment fragment = (SearchOracodeFragment) adapter.getItem(ViewPagerAdapter.SEARCH_FRAGMENT_INDEX);
        fragment.updateOracodeField(Utils.reverseOracode(oracode));
        viewPager.setCurrentItem(ViewPagerAdapter.SEARCH_FRAGMENT_INDEX, true);
    }

    @Override
    public void updateHistory() {
        HistoryFragment fragment = (HistoryFragment) adapter.getItem(ViewPagerAdapter.HISTORY_FRAGMENT_INDEX);
        fragment.updateHistory();
    }
}
