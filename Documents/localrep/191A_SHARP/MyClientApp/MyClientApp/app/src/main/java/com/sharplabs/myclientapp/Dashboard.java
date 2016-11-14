package com.sharplabs.myclientapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by gaspa_000 on 11/13/2016.
 */

public class Dashboard extends Fragment{

    View myView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.dashboard_layout, container, false);
        return myView;
    }

    public void onCreateMeetingClick() {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.content
                        , new Dashboard())
                .commit();
    }
}
