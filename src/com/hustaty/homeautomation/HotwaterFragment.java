package com.hustaty.homeautomation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.hustaty.homeautomation.model.StoredEventResult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * User: llisivko
 * Date: 2/24/13
 * Time: 7:20 PM
 */
public class HotwaterFragment extends Fragment {

    //Logging support.
    private static final String LOG_TAG = HotwaterFragment.class.getName();

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private EditText hotWaterValidFrom;

    private Calendar calendar;
    private SimpleDateFormat sdf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hotwater_fragment, container, false);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final EditText hotwaterValidity = (EditText) view.findViewById(R.id.hotwater_validity);
        final EditText hotwaterDateFrom = (EditText) view.findViewById(R.id.hotwater_date_from);
        hotwaterDateFrom.setText(sdf.format(calendar.getTime()));

        MyHttpClient myHttpClient = new MyHttpClient(view.getContext());

        try {
            List<StoredEventResult> storedEventResultList = myHttpClient.getStoredEventResults(Appliance.HOTWATER);
            if(storedEventResultList.size() > 0){
                //TODO
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (HomeAutomationException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        final ImageButton hotWaterSave = (ImageButton) view.findViewById(R.id.hotwater_save);

        hotWaterValidFrom = (EditText) view.findViewById(R.id.hotwater_date_from);

        ImageButton setDateTime = (ImageButton) view.findViewById(R.id.set_date_time);
        setDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(
                        view.getContext(),
                        new MyOnDateSetListener(),
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        hotWaterSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(calendar.getTime());
                cal2.add(Calendar.MINUTE, new Integer(hotwaterValidity.getText().toString()));

                MyHttpClient myHttpClient = new MyHttpClient(view.getContext());
                try {
                    CommonResult commonResult = myHttpClient.addStoredEvent(Appliance.HOTWATER, Command.HOTWATER_ON, calendar.getTime(), cal2.getTime());
                    Toast.makeText(view.getContext(), commonResult.getResult(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } catch (HomeAutomationException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });

        ImageButton resetHotWater = (ImageButton) view.findViewById(R.id.hotwater_reset);
        resetHotWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyHttpClient myHttpClient = new MyHttpClient(view.getContext());
                try {
                    CommonResult commonResult = myHttpClient.removeStoredEvent(Appliance.HOTWATER);
                    Toast.makeText(view.getContext(), commonResult.getResult(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } catch (HomeAutomationException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });

        return view;
    }

    private class MyOnDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            datePicker.updateDate(year, month, dayOfMonth);
            hotWaterValidFrom.setText(sdf.format(calendar.getTime()));
            datePickerDialog.hide();
            timePickerDialog = new TimePickerDialog(datePicker.getContext(), new MyOnTimeSetListener(), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }
    }

    private class MyOnTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            hotWaterValidFrom.setText(sdf.format(calendar.getTime()));
            timePickerDialog.hide();
        }
    }

}
