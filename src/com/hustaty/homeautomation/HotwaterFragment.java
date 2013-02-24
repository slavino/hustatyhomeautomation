package com.hustaty.homeautomation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.hustaty.homeautomation.enums.Appliance;
import com.hustaty.homeautomation.enums.Command;
import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.http.MyHttpClient;
import com.hustaty.homeautomation.model.CommonResult;

import java.io.IOException;
import java.util.Calendar;

/**
 * User: llisivko
 * Date: 2/24/13
 * Time: 7:20 PM
 */
public class HotwaterFragment extends Fragment {

    //Logging support.
    private static final String LOG_TAG = HotwaterFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hotwater_fragment, container, false);

        final DatePicker datePicker = (DatePicker)view.findViewById(R.id.hotwater_date_from);
        final TimePicker timePicker = (TimePicker)view.findViewById(R.id.hotwater_time_from);

        final ToggleButton wishedState = (ToggleButton)view.findViewById(R.id.hotwater_wished_state);

        final EditText hotwaterValidity = (EditText)view.findViewById(R.id.hotwater_validity);

        final ImageButton hotWaterSave = (ImageButton)view.findViewById(R.id.hotwater_save);

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

        hotWaterSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, datePicker.getYear());
                cal.set(Calendar.MONTH, datePicker.getMonth());
                cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                MyHttpClient myHttpClient = new MyHttpClient(view.getContext());
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(cal.getTime());
                cal2.add(Calendar.MINUTE, new Integer(hotwaterValidity.getText().toString()));
                try {
                    CommonResult commonResult = myHttpClient.addStoredEvent(Appliance.HOTWATER, (wishedState.isChecked() ? Command.HOTWATER_ON : Command.HOTWATER_OFF ), cal.getTime(), cal2.getTime());
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } catch (HomeAutomationException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }

            }
        });

        return view;
    }
}
