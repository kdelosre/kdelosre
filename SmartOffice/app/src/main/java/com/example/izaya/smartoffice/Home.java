package com.example.izaya.smartoffice;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Izaya on 11/11/2016.
 */

public class Home extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);
        return myView;
    }

//    public void onCreateMeeting(View v) {
//
//        if (v.getId() == R.id.createMeeting) {
//            FragmentManager fm = getFragmentManager();
//            fm.beginTransaction()
//                    .replace(R.id.nav_home_layout
//                            , new CreateMeeting())
//                    .commit();
//        }
//    }


}