package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.adapter.NavDrawerArrayAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.classes.NavDrawerItem;
import com.iaai.onyard.event.NavDrawerItemClickedEvent;

public class NavigationDrawerFragment extends BaseFragment {

    @InjectView(R.id.nav_drawer_list_primary)
    ListView mPrimaryList;
    @InjectView(R.id.nav_drawer_list_secondary)
    ListView mSecondaryList;

    public static NavigationDrawerFragment newInstance(ArrayList<NavDrawerItem> items) {
        final NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(IntentExtraKey.NAV_DRAWER_ITEMS, items);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_nav_drawer, container, false);
            ButterKnife.inject(this, view);

            initNavDrawer(getArguments().getParcelableArrayList(IntentExtraKey.NAV_DRAWER_ITEMS));

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    private void initNavDrawer(ArrayList<Parcelable> items) {
        NavDrawerItem navDrawerItem;
        final ArrayList<NavDrawerItem> primaryItems = new ArrayList<NavDrawerItem>();
        final ArrayList<NavDrawerItem> secondaryItems = new ArrayList<NavDrawerItem>();

        for (final Parcelable item : items) {
            navDrawerItem = (NavDrawerItem) item;

            switch (navDrawerItem.getType()) {
                case PRIMARY:
                    primaryItems.add(navDrawerItem);
                    break;
                case SECONDARY:
                    secondaryItems.add(navDrawerItem);
                    break;
                default:
                    break;
            }
        }

        final Activity activity = getActivity();
        if (activity != null) {
            mPrimaryList.setAdapter(new NavDrawerArrayAdapter(activity,
                    R.layout.primary_drawer_item, primaryItems));
            mPrimaryList.setOnItemClickListener(mPrimaryItemClickListener);

            mSecondaryList.setAdapter(new NavDrawerArrayAdapter(activity,
                    R.layout.secondary_drawer_item, secondaryItems));
            mSecondaryList.setOnItemClickListener(mSecondaryItemClickListener);
        }
    }

    private final OnItemClickListener mPrimaryItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (getActivity() != null) {
                getEventBus().post(new NavDrawerItemClickedEvent(position));
            }
        }
    };

    private final OnItemClickListener mSecondaryItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int numPrimary = mPrimaryList.getCount();

            if (getActivity() != null) {
                getEventBus().post(new NavDrawerItemClickedEvent(numPrimary + position));
            }
        }
    };
}
