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

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.gcm.GcmListenerService;

import cn.pedant.SweetAlert.SweetAlertDialog;
import listPage.ListPage;
import main.phoenix.R;

public class MyGcmListenerService extends GcmListenerService {
    Handler mHandler;
    private ListPage a;
    private static final String TAG = "MyGcmListenerService";
    int notifyID = 1; // 通知的識別號碼
    NotificationManager notificationManager;
    Notification notification ;
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
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
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
        Intent intent = new Intent(this, ListPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //final int notifyID = 1; // 通知的識別號碼
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pundo);
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
//        notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_launcher).setContentTitle("內容標題").setContentText("內容文字").setSound(defaultSoundUri).build(); // 建立通知
//        notification.defaults=Notification.DEFAULT_ALL;







                        //notificationManager.notify(notifyID, notification); // 發送通知


        new SweetAlertDialog(getBaseContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning")
                            .setContentText("You have missed your personal belongings")
                            .setConfirmText("OK")
                            .show();
                    notificationManager.notify(notifyID, notification); // 發送通知


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_edit)
                .setContentTitle("pundo message")
                .setContentText(message)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);



        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
