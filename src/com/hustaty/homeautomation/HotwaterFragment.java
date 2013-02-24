package com.hustaty.homeautomation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

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

        ToggleButton wishedState = (ToggleButton)view.findViewById(R.id.hotwater_wished_state);

        EditText hotwaterValidity = (EditText)view.findViewById(R.id.hotwater_validity);

        //default to next day at 07:15 in the morning
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH ,1);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 15);

        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

        hotwaterValidity.setText("60");

        wishedState.setChecked(true);

        return view;
    }
}
