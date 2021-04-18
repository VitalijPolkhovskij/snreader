/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pvs.snreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.pvs.snreader.ui.camera.CameraSource;
import com.pvs.snreader.ui.camera.CameraSourcePreview;
import com.pvs.snreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

import static com.pvs.snreader.MyConstants.autoSave;
import static com.pvs.snreader.MyConstants.customCases;
import static com.pvs.snreader.MyConstants.intAnySym;
import static com.pvs.snreader.MyConstants.setCases;
import static com.pvs.snreader.MyConstants.sizeCases;
import static com.pvs.snreader.MyConstants.valueSwitchNumber;
import static com.pvs.snreader.MyConstants.valueSwitchSN;
import static com.pvs.snreader.MyConstants.valueSwitchSNN;
import static com.pvs.snreader.MyConstants.valueswitchFRU;
import static com.pvs.snreader.MyConstants.valueswitchSERNO;
import static com.pvs.snreader.MyConstants.valueswitchSIN;
import static com.pvs.snreader.MyConstants.valueswitchSNID;
import static com.pvs.snreader.MyConstants.valueswitchSerialN;
import static com.pvs.snreader.MyConstants.onlySerialNumber;
import static com.pvs.snreader.MyConstants.titleText;
import static com.pvs.snreader.MyConstants.rotatedBitmap;
/**
 * Activity for the multi-tracker app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrCaptureActivity extends AppCompatActivity {

    private TextRecognizer textRecognizer;
    private String EPPV5;
    private String EPPV6;
    private String FRU;
    private String addString;
    private boolean ifUp;
    private boolean ifShow=true;

//    private static final String TAG = "OcrCaptureActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    static final String AutoFocus = "AutoFocus";
    static final String UseFlash = "UseFlash";
    static final String TextBlockObject = "String";

    private final int REQUEST_CAMERA = 101;
//    private TextBlock textResult = null;


    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private String val;
    private String delString;


    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.ocr_capture);

        rotatedBitmap=null;
        EPPV5 = null;
        EPPV6 = null;
        FRU = null;
        addString="";
        ifUp=false;


        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.graphicOverlay);

        // read parameters from the intent used to launch the activity.
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        if (!autoSave) {
            Snackbar.make(mGraphicOverlay, "Нажмите, чтобы захватить. Взять/растянуть, чтобы увеличить",
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
 //       Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = view -> ActivityCompat.requestPermissions(thisActivity, permissions,
                RC_HANDLE_CAMERA_PERM);

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));


        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
  //          Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
   //             Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.


  // Тут запись в файл - https://stackoverflow.com/questions/45225887/how-to-take-a-photo-using-camerasource


        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .build();

     //   mCameraSource.takePicture();

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
 //           Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
 //           Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

   //     Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
   //             " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = (dialog, id) ->
                finish();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("S/N reader")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            assert dlg != null;
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                if (mPreview != null)
                    mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
 //               Log.e(TAG, "Unable to start camera source.", e);

                Toast.makeText(getApplicationContext(),getString(R.string.unablestartcamera)+e.getMessage(),Toast.LENGTH_LONG).show();

                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and return it to
     * the Initializing Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        if (mGraphicOverlay == null)
            return false;
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock textResult = null;
        if (graphic != null) {
            textResult = graphic.getTextBlock();
            if (textResult != null) {

                takeImage(textResult.getValue());                        // PVS...
          }
            else {
//                Log.d(TAG, "text data is null");
            }
        }
        else {
//            Log.d(TAG,"no text detected");
        }
        return textResult != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (e != null) {
                return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
            }
            return false;
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (mCameraSource != null) {
                mCameraSource.doZoom(detector.getScaleFactor());
            }
        }
    }


    private void takeImage(final String result) {





        try{
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //releaseCameraSource();
            //releaseCamera();
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //setUpCamera(camera);
            //Thread.sleep(1000);
 //           mCameraSource.takePicture(shutterCallback , new CameraSource.PictureCallback() {
//           mCameraSource.takePicture(shutterCallback , new CameraSource.PictureCallback() {
          mCameraSource.takePicture(null, bytes -> {
              try {

                  rotatedBitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                  Intent data = new Intent();
                  data.putExtra(TextBlockObject, result);
                  setResult(CommonStatusCodes.SUCCESS, data);


/*
                  if (mPreview != null) {
                      mPreview.release();
                      mPreview = null;
                  }
*/

                  if (textRecognizer != null)
                      textRecognizer.release();
                  if (mGraphicOverlay != null)
                      mGraphicOverlay.clear();
                  finish();

              } catch (Exception e) {
                  e.printStackTrace();
              }
          });

        }catch (Exception ex){

            Toast.makeText(getApplicationContext(), getString(R.string.imagenotsaved),
                    Toast.LENGTH_SHORT).show();
        }

    }

//==========================================


    private int GetInt(String s_P) {
        if (s_P == null)
            return 0;
        if (s_P.length() == 0)
            return 0;
        int res = 0;
        try {
            res = Integer.parseInt(s_P);
        } catch (Exception e) {

//            ErParse(Fcontext,e.getMessage(),"GetInt!!!");
        }
        return res;
    }


    private boolean resultIf(String serN, int size) {
        if (val.contains(serN)) {
            int i=val.indexOf(serN);
            if (i != 0)
                val=val.substring(i);


            if (val.length() > (serN.length()+size)) {
               ifShow = false;
               titleText=serN;
               delString=serN;

            if (val.contains("\n")) {
                String[] tap = val.split("\n");
                val=tap[0];
            }

        return true;
        }
        }
     return false;
    }

    private boolean checkAnySym() {
        boolean ret=false;
        if (valueSwitchNumber) {
            if (!val.contains("\n")) {
                String resSerial = val.replace(" " , "");

                int v_L=resSerial.length();

                if (v_L == intAnySym)
                    ret=true;
            }
            else {
                String[] tst = val.split(" \n");
                for (String res: tst) {
                    String resSerial = res.replace(" " , "");
                    int v_L=resSerial.length();

                    if (v_L == intAnySym) {
                        val=res;
                        ret=true;
                        break;
                    }
                }
            }
        }
    return ret;
    }


    private boolean checkSerialN() {
        boolean ret=false;
        if (valueswitchSerialN && val.contains("SERIAL N")) {

            if (val.length() > ("SERIAL N".length()+5)) {
            if (resultIf("SERIAL NUMBER:",5)) {
                ret=true;
            }
            else if (resultIf("SERIAL NUMBER",5)) {
                ret=true;
            }
            else if (resultIf("SERIAL N:",5)) {
                ret=true;
            }
            else if (resultIf("SERIAL N",5)) {
                ret=true;
            }
            else if (resultIf("SERIAL NUM:",5)) {
                ret=true;
            }
            else if (resultIf("SERIAL NUM",5)) {
                ret=true;
            }
        }
        }

        return ret;
    }


    private class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

        private GraphicOverlay<OcrGraphic> mGraphicOverlay;

        OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
            mGraphicOverlay = ocrGraphicOverlay;
        }

        /**
         * Called by the detector to deliver detection results.
         * If your application called for it, this could be a place to check for
         * equivalent detections by tracking TextBlocks that are similar in location and content from
         * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
         * multiple detections.
         */


        int odt = 1;

        @Override
        public void receiveDetections(@NonNull Detector.Detections<TextBlock> detections) {

            if (odt == 0) {
                mGraphicOverlay.clear();
                SparseArray<TextBlock> items = detections.getDetectedItems();
                for (int i = 0; i < items.size(); ++i) {
                    TextBlock item = items.valueAt(i);
                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item,false);
                    mGraphicOverlay.add(graphic);
                }
            return;
            }



            mGraphicOverlay.clear();
            if (ifUp)
                return;
            SparseArray<TextBlock> items = detections.getDetectedItems();
            for (int i = 0; i < items.size(); ++i) {





                TextBlock item = items.valueAt(i);
/*
                String s_odt = item.getValue().toUpperCase();
                if (s_odt.contains("S/N") || s_odt.contains("SN") || s_odt.contains("SIN"))
                    odt++;
*/

                ifShow=true;
                if (onlySerialNumber) {
                    delString="";
                    String resSerial="";
                    val = item.getValue().toUpperCase();








                    if (val.contains("EPPV5")) {
                        EPPV5="EPPV5";
                        if (onlySerialNumber)
                            continue;
                    }
                    if (val.contains("EPPV6")) {
                        EPPV5="EPPV6";
                        if (onlySerialNumber)
                            continue;
                    }

                    else if (valueswitchFRU && val.contains("FRU:") && val.length() > 9) {
                        FRU="FRU:";
                        if (onlySerialNumber)
                            continue;
                    }
                    else if (valueswitchFRU && val.contains("FRU :") && val.length() > 10) {
                        FRU="FRU :";
                        if (onlySerialNumber)
                            continue;
                    }

                    else if (addString.length() == 0 && (EPPV5 != null || EPPV6 != null) && (val.contains("RU ") || val.contains("UA "))) {
                        addString=val.substring(3);
                        if (onlySerialNumber)
                            continue;

                    }

                    String[] apt = val.split("-");


                    if (apt.length == 3) {
                        if (FRU != null && val.contains("DOPPELABZ")) {
                                if (onlySerialNumber)
                                    continue;
                            }
                        }


                    String[] tst = val.split(" ");
                    boolean ifLong=true;
                    if (tst.length == 4) {
                        for (String crTest : tst) {
                           if (crTest.length() != 4) {
                               ifLong = false;
                               break;
                           }
                        }
                    }
                    else
                        ifLong=false;

                    if (ifLong) {
                        titleText="";
                        ifShow = false;
                    }

                    else if (FRU != null && apt.length == 3) {
                        titleText=FRU;
                        ifShow = false;
                        resSerial=apt[1]+"-"+apt[2];
                    }

                    else if (checkAnySym()) {
                        ifShow = false;
                    }
                    else if ((EPPV5 != null || EPPV6 != null) &&
                            val.length() == 8 &&

                            addString.length() != 0 &&
                            !val.contains("\n") &&
                            !val.contains("-") &&
                            GetInt(val) != 0) {
                        ifShow = false;
                        if (EPPV5 != null)
                            titleText="EPPV5";
                        else
                            titleText="EPPV6";
                        delString="";
                    }

// Добавь ключ одно число...

                    else if (valueSwitchSN && resultIf("S/N:",5)) {
                        ifShow = false;
                    }
                    else if (valueSwitchSNN && resultIf("SN:",5)) {
                        ifShow = false;
                    }

// Можно считывать сразу нескольо строк... С Sn, Sin и т.д.

                    else if (valueswitchSIN && resultIf("SIN:",5)) {
                        ifShow = false;
                    }
                    else if (valueswitchSIN && resultIf("SIN :",5)) {
                        ifShow = false;
                    }
                    else if (valueswitchSNID && resultIf("SNID:",5)) {
                        ifShow = false;
                    }
                    else if (valueswitchSNID && resultIf("SNID :",5)) {
                        ifShow = false;
                    }
                    else if (valueSwitchSN && resultIf("S/N :",5)) {
                    }
                    else if (valueswitchSERNO && resultIf("SER NO",5)) {
                    }

// Serial Nuber, Serial N можно обьединить в один...


                    else if (checkSerialN()) {
                        ifShow = false;
                    }
                    else if (customCases.size() != 0) {
                        for (int w=0; w < customCases.size(); w++) {
                            boolean ifS = setCases.get(w);
                            if (ifS) {
                                String name=customCases.get(w).toUpperCase();
                                int size= sizeCases.get(w);
                                if (resultIf(name,size))
                                    break;
                            }
                        }
                    }
                    if (!ifShow) {

                        if (autoSave) {
                            ifUp=true;
//                            textRecognizer.release();
//                            mGraphicOverlay.clear();


//                            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item,true);
//                            mGraphicOverlay.add(graphic);


                            if (FRU == null) {
                                if (delString.length() != 0)
                                    resSerial = val.substring(delString.length());
                                else
                                    resSerial = val;
                                resSerial = addString + resSerial;
                            }

                            resSerial = resSerial.replace(" " , "");
                            takeImage(resSerial);

                        }

                    }

                }
                if (ifShow) {
                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item,false);
                    mGraphicOverlay.add(graphic);
                }
            }
        }

        /**
         * Frees the resources associated with this detection processor.
         */
        @Override
        public void release() {
            if (mGraphicOverlay != null) {
                mGraphicOverlay.clear();
            }
        }
    }


    @Override
    public void onBackPressed() {

/*
        if (mPreview != null) {
            mPreview.release();
            mPreview = null;
        }
*/
        super.onBackPressed();
    }
}
