package com.hustaty.homeautomation.service;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;
import com.hustaty.homeautomation.util.LogUtil;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.richnotification.*;
import com.samsung.android.sdk.richnotification.templates.SrnPrimaryTemplate;
import com.samsung.android.sdk.richnotification.templates.SrnSecondaryTemplate;
import com.samsung.android.sdk.richnotification.templates.SrnStandardSecondaryTemplate;
import com.samsung.android.sdk.richnotification.templates.SrnStandardTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by slavomirhustaty on 29/09/2016.
 */
public class SamsungRichNotificationService {

    private static final String LOG_TAG = SamsungRichNotificationService.class.getName();

    private final Context mContext;
    private SrnRichNotificationManager mRichNotificationManager;

    public SamsungRichNotificationService(Context ctx, ArduinoThermoServerStatus arduinoThermoServerStatus, String notificationText) {
        mContext = ctx;
        Srn srn = new Srn();
        try {
            // Initialize an instance of Srn.
            srn.initialize(ctx);
        } catch (SsdkUnsupportedException e) {
            // Error handling
            Log.e(LOG_TAG, e.getMessage());
            LogUtil.appendLog(LOG_TAG + "#SamsungRichNotificationService(): " + e.getMessage());
        }
        mRichNotificationManager = new SrnRichNotificationManager(ctx);
        mRichNotificationManager.start();
        UUID uuid = mRichNotificationManager.notify(createRichNoti(arduinoThermoServerStatus, notificationText));
        mRichNotificationManager.stop();
    }

    public SrnRichNotification createRichNoti(ArduinoThermoServerStatus arduinoThermoServerStatus, String notificationText) {
        SrnRichNotification noti = new SrnRichNotification(mContext);
        noti.setReadout("Received Push notification",
                notificationText);
        noti.setPrimaryTemplate(getSmallHeaderTemplate(arduinoThermoServerStatus));

        noti.setSecondaryTemplate(getSmallSecondaryTemplate(arduinoThermoServerStatus));

        noti.setTitle("Hustaty Home Automation");
        try {
            noti.addActionsWithPermissionCheck(getActions());
        } catch(Exception e) {
            Log.e(LOG_TAG,e.getMessage());
            LogUtil.appendLog(LOG_TAG + "#createRichNoti(): " + e.getMessage());
        }
        noti.setAlertType(SrnRichNotification.AlertType.VIBRATION);

        Bitmap appIconBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.home);
        SrnImageAsset appIcon = new SrnImageAsset(mContext, "app_icon", appIconBitmap);
        noti.setIcon(appIcon);

        return noti;
    }

    public SrnPrimaryTemplate getSmallHeaderTemplate(ArduinoThermoServerStatus arduinoThermoServerStatus) {
        SrnStandardTemplate smallHeaderTemplate = new SrnStandardTemplate(SrnStandardTemplate.HeaderSizeType.SMALL);

        smallHeaderTemplate.setSubHeader("<b>HHA Information</b>");
        if(arduinoThermoServerStatus != null) {
            smallHeaderTemplate.setBody(arduinoThermoServerStatus.toHTMLString());
        } else {
            smallHeaderTemplate.setBody("Empty");
        }
        smallHeaderTemplate.setBackgroundColor(Color.rgb(0, 0, 0));

        return smallHeaderTemplate;
    }

    public SrnSecondaryTemplate getSmallSecondaryTemplate(ArduinoThermoServerStatus arduinoThermoServerStatus) {
        SrnStandardSecondaryTemplate smallSecTemplate = new SrnStandardSecondaryTemplate();

        smallSecTemplate.setTitle("<b>HHA Information</b>");

        smallSecTemplate.setBackgroundColor(Color.rgb(0, 0, 0));
        smallSecTemplate.setSubHeader("<b>Release Date</b>");

        smallSecTemplate.setBody("FOR LATER USE");

//        Bitmap qrCodeBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.home);
//        SrnImageAsset qrCodeBig = new SrnImageAsset(mContext, "qr_code_big", qrCodeBitmap);
//        smallSecTemplate.setImage(qrCodeBig);

//        Bitmap commentBM = BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.home);
//        SrnImageAsset commentIcon = new SrnImageAsset(mContext, "comment_icon", commentBM);
//        smallSecTemplate.setSmallIcon1(commentIcon, "4/5");

//        Bitmap likeBM = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.home);
//        SrnImageAsset likeIcon = new SrnImageAsset(mContext, "like_icon", likeBM);
//        smallSecTemplate.setSmallIcon2(likeIcon, "999+");

        return smallSecTemplate;
    }

    public List<SrnAction> getActions() {
        ArrayList<SrnAction> myActions = new ArrayList<SrnAction>();

//        SrnHostAction primaryAction = new SrnHostAction("Listen On Phone");
//        Bitmap listenBitmap = BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.listen);
//        SrnImageAsset listenIcon = new SrnImageAsset(mContext, "web_icon", listenBitmap);
//
//        String url = "http://musicmp3.ru/artist_taylor-swift__album_red.html#.U-Cj3WPzkjY";
//        Intent resultIntent = new Intent(mContext, MyCallbackActivity.class);
//        resultIntent.setData(Uri.parse(url));
//
//        primaryAction.setIcon(listenIcon);
//        primaryAction.setToast("Music will be played on Phone!");
//        primaryAction.setCallbackIntent(SrnAction.CallbackIntent.getActivityCallback(resultIntent));
//        myActions.add(primaryAction);

//        SrnHostAction action2 = new SrnHostAction("Watch On Phone");
//        String yturl = "http://www.youtube.com/watch?v=Smu1jse33bQ";
//        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(yturl));
//        videoIntent.setData(Uri.parse(url));

//        Bitmap watctBitmap = BitmapFactory
//                .decodeResource(mContext.getResources(), R.drawable.watch);
//        SrnImageAsset watchIcon = new SrnImageAsset(mContext, "web_icon", watctBitmap);
//        action2.setIcon(watchIcon);
//        action2.setToast("Youtube App will be launched on Phone!");
//        action2.setCallbackIntent(SrnAction.CallbackIntent.getActivityCallback(videoIntent));
//        myActions.add(action2);

//        SrnRemoteInputAction keyboardAction = new SrnRemoteInputAction("Comment");
//        SrnRemoteInputAction.KeyboardInputMode kInputMode = SrnRemoteInputAction.InputModeFactory.createKeyboardInputMode()
//                .setPrefillString("@name")
//                .setCharacterLimit(140)
//                .setKeyboardType(SrnRemoteInputAction.KeyboardInputMode.KeyboardType.NORMAL);
//
//        keyboardAction.setRequestedInputMode(kInputMode);
//
//        Intent keyboardIntent = new Intent(
//                "com.samsung.android.richnotification.sample.callback_broadcast");
//        keyboardAction.setCallbackIntent(SrnAction.CallbackIntent.getBroadcastCallback(keyboardIntent));
//
//        myActions.add(keyboardAction);

        return myActions;
    }

}
