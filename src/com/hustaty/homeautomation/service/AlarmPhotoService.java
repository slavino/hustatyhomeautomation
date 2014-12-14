package com.hustaty.homeautomation.service;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;
import com.hustaty.homeautomation.handler.PhotoHandler;

/**
 * Created by slavino on 12/14/14.
 */
public class AlarmPhotoService {

    private int cameraId = 0;

    private Camera camera;

    private static final String LOG_TAG = AlarmPhotoService.class.getName();

    public AlarmPhotoService(Context context) {
        if (!context.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.i(LOG_TAG, "No camera found on device");
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(context, "No front facing camera found.", Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
                camera.takePicture(null, null, new PhotoHandler(context));
                camera.release();
                camera = null;
            }
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(LOG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}
