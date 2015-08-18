package com.hustaty.homeautomation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import com.hustaty.homeautomation.util.LogUtil;

import java.io.InputStream;

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

    // logger entry
    private final static String LOG_TAG = HeatingFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.heating_fragment, container, false);

        new DownloadImageTask((ImageView) view.findViewById(R.id.workingRoomGraph))
                .execute("https://api.xively.com/v2/feeds/84966/datastreams/t280F5B8504000019.png?w=800&h=300&b=true&g=true&t=Pracovna&start=2015-08-17T00:00:00+01:00&end=2015-08-17T23:59:59+01:00&timezone=Berlin");

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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap resultBitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                resultBitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                LogUtil.appendLog(LOG_TAG + " #doInBackground(): " + e.getMessage());
            }
            return resultBitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
