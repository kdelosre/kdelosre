package com.example.izaya.smartoffice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Izaya on 11/11/2016.
 */

public class MeetingDashboard extends Fragment {

        private FragmentTabHost mTabHost;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mTabHost = new FragmentTabHost(getActivity());
            mTabHost.setup(getActivity(), getChildFragmentManager(), R.layout.dashboard_layout);

            Bundle arg1 = new Bundle();
//            arg1.putInt("Arg for Frag1", 1);
            mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("Timeline"),
                    TimelineView.class, arg1);

            Bundle arg2 = new Bundle();
//            arg2.putInt("Arg for Frag2", 2);
            mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("Files"),
                    FilesView.class, arg2);

            Bundle arg3 = new Bundle();
//            arg2.putInt("Arg for Frag3", 3);
            mTabHost.addTab(mTabHost.newTabSpec("Tab3").setIndicator("Chat"),
                    SecondFragment.class, arg2);

            return mTabHost;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            mTabHost = null;
        }






}
