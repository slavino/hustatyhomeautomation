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
import android.widget.RelativeLayout;
import com.hustaty.homeautomation.util.CommonUtil;
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

        new DownloadImageTask((ImageView) view.findViewById(R.id.heatingStateGraph)).execute(CommonUtil.getXivelyDatastreamURL("84966", "heatingState", "Heating","ff0000"));
        new DownloadImageTask((ImageView) view.findViewById(R.id.southChldrmGraph)).execute(CommonUtil.getXivelyDatastreamURL("84966", "t288b4c5605000020", "South_childroom"));
        new DownloadImageTask((ImageView) view.findViewById(R.id.northChldrmGraph)).execute(CommonUtil.getXivelyDatastreamURL("84966", "t28e6c455050000d4", "North_childroom"));
        new DownloadImageTask((ImageView) view.findViewById(R.id.bedroomGraph)).execute(CommonUtil.getXivelyDatastreamURL("84966", "t282a54ab0400004e", "Bedroom"));
        new DownloadImageTask((ImageView) view.findViewById(R.id.bedroomCeilingGraph)).execute(CommonUtil.getXivelyDatastreamURL("84966", "t28B79F8504000082", "Bedroom_Ceiling"));
        new DownloadImageTask((ImageView) view.findViewById(R.id.kitchenGraph)).execute(CommonUtil.getXivelyDatastreamURL("84966", "t28C9C9AA040000EA", "Kitchen"));
        new DownloadImageTask((ImageView) view.findViewById(R.id.workRoomGraph)).execute(CommonUtil.getXivelyDatastreamURL("84966", "t280F5B8504000019", "Workroom"));
        new DownloadImageTask((ImageView) view.findViewById(R.id.outsideGraph)).execute(CommonUtil.getXivelyDatastreamURL("84966", "t28F82D850400001F", "Outside","00ff00"));

        //HACK for devices without MENU button
        Button settingsButton = (Button) view.findViewById(R.id.settingsLaunch);

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
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
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
            imageView.setImageBitmap(result);
        }
    }
}
