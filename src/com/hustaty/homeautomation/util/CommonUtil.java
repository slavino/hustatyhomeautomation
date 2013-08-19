package com.hustaty.homeautomation.util;

import android.content.Context;
import android.content.Intent;

/**
 * User: hustasl
 * Date: 3/28/13
 * Time: 5:17 PM
 */
public class CommonUtil {

  /**
   * Intent used to display a message in the screen.
   */
  public static final String DISPLAY_MESSAGE_ACTION = "com.hustaty.homeautomation.DISPLAY_MESSAGE";

  /**
   * Intent's extra that contains the message to be displayed.
   */
  public static final String EXTRA_MESSAGE = "message";

  /**
   * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
   */
  public static final String SERVER_URL = (String) ApplicationPreferences.getPreferences().get("gcm_sender_url");

  /**
   * Google API project id registered to use GCM.
   */
  public static final String SENDER_ID = (String) ApplicationPreferences.getPreferences().get("gcm_sender_id");


  /**
   * Notifies UI to display a message.
   * <p/>
   * This method is defined in the common helper because it's used both by
   * the UI and the background service.
   *
   * @param context application's context.
   * @param message message to be displayed.
   */
  public static void displayMessage(Context context, String message) {
    Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
    intent.putExtra(EXTRA_MESSAGE, message);
    context.sendBroadcast(intent);
  }

}
