package com.example.mydictionary;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.urbanairship.UAirship;

import org.json.JSONObject;

/**
 * Created by huynhpro on 4/29/2016.
 */
public class PushNotification extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .init();

        UAirship.takeOff(this, new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(UAirship airship) {

                // Enable user notifications
                airship.getPushManager().setUserNotificationsEnabled(true);


            }
        });
    }

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            try {
                if (additionalData != null) {
                    if (additionalData.has("actionSelected"))
                        Log.d("OneSignalExample", "OneSignal notification button with id " + additionalData.getString("actionSelected") + " pressed");

                    Log.d("OneSignalExample", "Full additionalData:\n" + additionalData.toString());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

            // The following can be used to open an Activity of your choose.
               /*
                 Intent intent = new Intent(getApplication(), YourActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
               */
            // Follow the insturctions in the link below to prevent the launcher Activity from starting.
            // https://documentation.onesignal.com/docs/android-notification-customizations#changing-the-open-action-of-a-notification
        }
    }
}
