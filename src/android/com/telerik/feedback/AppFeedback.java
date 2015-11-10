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

import com.telerik.widget.feedback.RadFeedback;

public class AppFeedback extends CordovaPlugin implements RadFeedback.OnSendFeedbackFinishedListener {
    private RadFeedback radFeedback;
    private boolean shouldShowFeedback;

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
                } catch(Exception ex) {
                }
            }
            callbackContext.success(data);
            return true;
        }
        if (action.equalsIgnoreCase("initialize")) {
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
        }
    }
    
    private void showFeedback() {
        if (this.radFeedback != null && this.shouldShowFeedback == true) {
            this.shouldShowFeedback = false;
            this.radFeedback.show(this.cordova.getActivity());
        }
    }
}