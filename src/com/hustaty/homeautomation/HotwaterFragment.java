package com.hustaty.homeautomation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: llisivko
 * Date: 2/24/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class HotwaterFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.hotwater_fragment, container, false);

        return view;
    }
}
