package com.iaai.onyard.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.iaai.onyard.R;
import com.iaai.onyard.activity.fragment.NavigationDrawerFragment;
import com.iaai.onyard.activity.fragment.PageFragment;
import com.iaai.onyard.adapter.SwipePagerAdapter;
import com.iaai.onyard.classes.NavDrawerItem;
import com.iaai.onyard.transformer.SwipePageTransformer;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.viewpagerindicator.UnderlinePageIndicator;

public abstract class PagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private SwipePagerAdapter mPagerAdapter;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mDrawerFrame;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mDrawerLayout.isDrawerVisible(mDrawerFrame) && keyCode == KeyEvent.KEYCODE_BACK) {
            closeNavDrawer();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    protected void initializePager() {
        mPagerAdapter = new SwipePagerAdapter(
                getSupportFragmentManager(),
                AuthenticationHelper.isAnyUserLoggedIn(getContentResolver()) ? getLoggedInPagerFragments()
                        : getLoggedOutPagerFragments());
        mViewPager = (ViewPager) findViewById(getViewPagerId());
        mViewPager.setPageTransformer(true, new SwipePageTransformer());
        mViewPager.setOffscreenPageLimit(10);
        mViewPager.setBackgroundColor(Color.BLACK);
        mViewPager.setAdapter(mPagerAdapter);

        final UnderlinePageIndicator pageIndicator = (UnderlinePageIndicator) findViewById(R.id.activity_search_page_indicator);
        pageIndicator.setViewPager(mViewPager);
        pageIndicator.setOnPageChangeListener(this);
    }

    protected void initializeNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(getDrawerLayoutId());
        mDrawerFrame = (FrameLayout) findViewById(getDrawerFrameId());

        getSupportFragmentManager().beginTransaction()
        .replace(getDrawerFrameId(),
                        NavigationDrawerFragment.newInstance(getNavDrawerItemList()))
                .commitAllowingStateLoss();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.nav_drawer_open,
                R.string.nav_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // required for the drawer indicator to show up
        mDrawerLayout.post(new Runnable() {

            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    protected abstract ArrayList<NavDrawerItem> getNavDrawerItemList();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(getActionBarMenuId(), menu);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getCurrentPage().updateOptionsMenu();
        return true;
    }

    protected abstract List<Fragment> getLoggedOutPagerFragments();

    protected abstract List<Fragment> getLoggedInPagerFragments();

    protected abstract int getViewPagerId();

    protected abstract int getActionBarMenuId();

    protected PageFragment getCurrentPage() {
        return (PageFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    protected void pageToPosition(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    protected int getCurrentPagePosition() {
        return mViewPager.getCurrentItem();
    }

    protected abstract int getDrawerLayoutId();

    protected abstract int getDrawerFrameId();

    protected void closeNavDrawer() {
        mDrawerLayout.closeDrawer(mDrawerFrame);
    }

    protected boolean isNavDrawerVisible() {
        return mDrawerLayout.isDrawerVisible(mDrawerFrame);
    }

    @Override
    public void onPageSelected(int newPosition) {
        getCurrentPage().onBecomeCurrentPage();
        getCurrentPage().updateOptionsMenu();
        getCurrentPage().hideSoftKeyboard();

        initializeNavDrawer();
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {}

    @Override
    public void onPageScrollStateChanged(int arg0) {}
}
