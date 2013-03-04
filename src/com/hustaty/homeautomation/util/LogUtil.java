package com.hustaty.homeautomation.util;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: hustasl
 * Date: 3/4/13
 * Time: 2:22 PM
 */
public class LogUtil {

    //Logging purposes
    private static final String LOG_TAG = LogUtil.class.getName();

    private static void checkFileExists() {
        File logFile = new File(logFileLocation());
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e(LOG_TAG, "#checkFileExists(): " + e.getMessage());
            }
        }
    }

    public static void appendLog(String text) {
        File logFile = new File(logFileLocation());

        checkFileExists();

        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append((new Date()) + " : " + text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "#appendLog(): " + e.getMessage());
        }
    }

    public static String readLog() {
        String result = "";
        File logFile = new File(logFileLocation());
        checkFileExists();
        try {
            BufferedReader buf = new BufferedReader(new FileReader(logFile));
            String line;
            while((line = buf.readLine()) != null) {
                result += line + "\n";
            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, ":#readLog(): " + e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG ,"#readLog(): " + e.getMessage());
        }
        return result;
    }

    public static void clearLog() {
        File logFile = new File(logFileLocation());
        logFile.delete();
        checkFileExists();
    }

    public static void checkFileTooOldOrLong() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);

        File logFile = new File(logFileLocation());

        Date lastModified = new Date(logFile.lastModified());

        if(lastModified.before(cal.getTime())) {
            clearLog();
        }
    }

    public static String getLogfilelocation() {
        return LogUtil.logFileLocation();
    }

    public static Uri getLogFileUri() {
        return LogUtil.logFileUri();
    }

    private static String logFileLocation() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return Environment.getExternalStorageDirectory()
                + File.separator
                + "hustatyhomeautomation"
                + simpleDateFormat.format(new Date()) + ".log";
    }

    private static Uri logFileUri() {
        return Uri.fromFile(new File(logFileLocation()));
    }


}

