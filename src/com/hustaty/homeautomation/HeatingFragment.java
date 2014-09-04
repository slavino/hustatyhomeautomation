package com.hustaty.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

//import javax.crypto.*;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;

/**
 * User: llisivko
 * Date: 2/24/13
 * Time: 7:20 PM
 */
public class HeatingFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.heating_fragment, container, false);

        //HACK for devices without MENU button
        Button settingsButton = (Button)view.findViewById(R.id.settingsLaunch);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

//        NumberPicker numberPicker = (NumberPicker)view.findViewById(R.id.temperature);
//        numberPicker.setDisplayedValues(new String[]{"15","16","17","18"});

        return view;
    }

}
