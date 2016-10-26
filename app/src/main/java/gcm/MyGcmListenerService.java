/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;


import org.json.JSONArray;

import listPage.ListPage;
import main.phoenix.R;

public class MyGcmListenerService extends GcmListenerService {
    LocalBroadcastManager broadcaster ;
    static final public String COPA_RESULT = "gg";
    static final public String COPA_MESSAGE = "gcm.MyGcmListenerService.COPA_MSG";


    private static final String TAG = "MyGcmListenerService";


    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */

    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String item = "";
        String message = data.getString("message");
        if(data.getString("item") != null)
            item = data.getString("item").substring(1,data.getString("item").length()-1);

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "ITEM2: " + item);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        Intent intent = new Intent(this, ListPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pundo);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_edit)
                .setContentTitle("pundo message")
                .setContentText(message)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setOngoing(false)
                .setDefaults(Notification.FLAG_INSISTENT)
                .setFullScreenIntent(pendingIntent,true)
                .setSound(defaultSoundUri, RingtoneManager.TYPE_RINGTONE)
                .setContentIntent(pendingIntent);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


        if(!ListPage.mIsPause){
            Intent in = new Intent(COPA_RESULT);
            if(message != null) {
                if(message.equals("forget")) {
                    in.putExtra(COPA_MESSAGE, message);
                    in.putExtra("item", item);
                }else {
                    in.putExtra(COPA_MESSAGE, message);
                }

            }
            broadcaster.sendBroadcast(in);
        }else{
            switch (message) {
                case "pm":
                    ListPage.hasToShowDialogPM = true;
                    break;
                case "noPm":
                    ListPage.hasToShowDialogNoPM = true;
                    break;
                case "rain":
                    ListPage.hasToShowDialogRain = true;
                    break;
                case "noRain":
                    ListPage.hasToShowDialogNoRain = true;
                    break;
                case "forget":
                    ListPage.hasToSHowDialogForgot = true;
                    break;
            }
        }
        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {







    }

}
