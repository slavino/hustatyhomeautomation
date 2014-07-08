package com.hustaty.homeautomation;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by user on 08-Jul-14.
 */
public abstract class AbstractHomeAutomationFragment extends Fragment {

    private ProgressDialog progressDialog;
    private final static String LOG_TAG = AbstractHomeAutomationFragment.class.getName();

    public void showLoadingProgressDialog() {
        Log.d(LOG_TAG, "Showing progress dialog.");
        this.showProgressDialog("Loading. Please wait...");
    }

    public void showProgressDialog(CharSequence message) {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this.getActivity());
            this.progressDialog.setIndeterminate(true);
        }

        this.progressDialog.setMessage(message);
        Log.d(LOG_TAG, "Showing progress dialog with message['" + message + "']");
        this.progressDialog.show();
    }

    public void dismissProgressDialog() {
        Log.d(LOG_TAG, "Dismissing progress dialog.");
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
    }

}
