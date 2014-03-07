package com.hustaty.homeautomation.service;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by user on 3/7/14.
 */
public class TTSService implements TextToSpeech.OnInitListener {

    //logging support
    private static final String LOG_TAG = TTSService.class.getName();

    private TextToSpeech tts;

    private String textToBeSpoken;

    private final Context context;

    private Boolean initialized = false;

    public TTSService(Context context) {
        this.context = context;
        tts = new TextToSpeech(context, this);
        this.textToBeSpoken = "";
    }

    public void setTextToBeSpoken(String textToBeSpoken) {
        this.textToBeSpoken = textToBeSpoken;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(LOG_TAG, "This Language is not supported");
            } else {
                this.initialized = true;
                Log.i(LOG_TAG, "TTS initialized.");
                speakText(textToBeSpoken);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakText(String text) {
        if (this.initialized) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            while(tts.isSpeaking()) {
                //do nothing
            };
            tts.stop();
            tts.shutdown();
            Log.d(LOG_TAG, "TTS Shutdown");
        }
    }

}
