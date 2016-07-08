package com.telerik.feedback;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.graphics.Bitmap;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

import com.telerik.widget.feedback.BitmapResolver;
import com.telerik.widget.feedback.RadFeedback;

public class AppFeedback extends CordovaPlugin implements RadFeedback.OnSendFeedbackFinishedListener {
    private RadFeedback radFeedback;
    private boolean shouldShowFeedback;
    private static boolean isCrosswalk;
    private CountDownLatch crosswalkLatch;
    private Bitmap crosswalkScreenshot;

    // Check if the app uses Crosswalk
    static {
        try {
            Class.forName("org.crosswalk.engine.XWalkWebViewEngine");
            isCrosswalk = true;
        } catch (Exception ignore) {
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("GetVariables")) {
            JSONObject data = new JSONObject();
            for (int i = 0; i < args.length(); i++) {
                try {
                    String variableName = args.getString(i);
                    int appResId = cordova.getActivity().getResources().getIdentifier(variableName, "string", cordova.getActivity().getPackageName());
                    String variableValue = cordova.getActivity().getString(appResId);
                    data.put(variableName, variableValue);
                } catch (Exception ex) {
                }
            }
            callbackContext.success(data);
            return true;
        } else if (action.equalsIgnoreCase("initialize")) {
            this.initialize(args);
        } else if (action.equalsIgnoreCase("showfeedback")) {
            this.showFeedback();
        } else {
            callbackContext.error("Method not found");
            return false;
        }

        callbackContext.success();
        return true;
    }

    @Override
    public void sendFeedbackFinished() {
        this.shouldShowFeedback = true;
    }

    private void initialize(JSONArray args) throws JSONException {
        if (this.radFeedback == null) {
            String apiKey = args.getString(0);
            String apiBaseUrl = args.getString(1);
            String uid = Settings.Secure.getString(this.cordova.getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            this.shouldShowFeedback = true;

            this.radFeedback = RadFeedback.instance();
            this.radFeedback.setOnFeedbackFinishedListener(this);
            this.radFeedback.init(apiKey, apiBaseUrl, uid);

            // Change the default BitmapResolver if the app uses Crosswalk
            if (isCrosswalk) {
                this.radFeedback.setBitmapResolver(new BitmapResolverCrosswalk());
            }
        }
    }

    private class BitmapResolverCrosswalk implements BitmapResolver {
        @Override
        public Bitmap getBitmapFromView(View view) {
            AppFeedback.this.crosswalkLatch = new CountDownLatch(1);
            webView.getPluginManager().postMessage("captureXWalkBitmap", this);
            try {
                AppFeedback.this.crosswalkLatch.await(5L, TimeUnit.SECONDS);
            } catch (Exception e) {
                AlertDialog.Builder latchErrorBuilder = new AlertDialog.Builder(AppFeedback.this.cordova.getActivity());
                latchErrorBuilder.setTitle("AppFeedback Error");
                latchErrorBuilder.setMessage("" + e);
                latchErrorBuilder.setCancelable(false);
                latchErrorBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog latchError = latchErrorBuilder.create();
                latchError.show();
            }
            return AppFeedback.this.crosswalkScreenshot;
        }
    }

    // Handle the screenshot from Crosswalk
    @Override
    public Object onMessage(String id, Object data) {
        if (id.equals("onGotXWalkBitmap")) {
            this.crosswalkScreenshot = (Bitmap) data;
            this.crosswalkLatch.countDown();
        }
        return null;
    }

    private void showFeedback() {
        if (this.radFeedback != null && this.shouldShowFeedback == true) {
            this.shouldShowFeedback = false;
            this.radFeedback.show(this.cordova.getActivity());
        }
    }
}