package com.hustaty.homeautomation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

/**
 * User: llisivko
 * Date: 2/24/13
 * Time: 7:20 PM
 */
public class HotwaterFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hotwater_fragment, container, false);

        DatePicker datePicker = (DatePicker)view.findViewById(R.id.hotwater_date_from);
        TimePicker timePicker = (TimePicker)view.findViewById(R.id.hotwater_time_from);
//        NumberPicker numberPicker = (NumberPicker)view.findViewById(R.id.hotwater_validity);


        return view;
    }
}
