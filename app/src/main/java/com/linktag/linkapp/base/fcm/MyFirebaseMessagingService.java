/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.linktag.linkapp.base.fcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.linktag.base.util.ClsNotificationChannel;
import com.linktag.linkapp.BuildConfig;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.login.Login;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 53489;
    public static final int NOTIFICATION_CHANGE = 5339;
    private static final String TAG = "MyFirebaseMsgService";


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.sendRegistrationToServer
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            try {
                Intent intent = new Intent();

                for (String key : remoteMessage.getData().keySet()) {
                    intent.putExtra(key, remoteMessage.getData().get(key));
                }

                showMessage(this, intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //showMessage(this, remoteMessage.getData().get("message"));


//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
            // Check if message contains a notification payload.
        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Log.d("Test", "sendRegistrationToServer : " + token);
    }


    private void showMessage(Context context, Intent intent) {
        try {
            String title = intent.getStringExtra("DATA_TITLE");
            String text = intent.getStringExtra("DATA_TEXT");
            String tickerText = intent.getStringExtra("DATA_TICKER");
            boolean isPushOn = intent.getBooleanExtra("DATA_IS_PUSH_ON", false);

            if (BuildConfig.DEBUG) {
                title = "제목";
                text = "메시지";
                tickerText = "간단한 설명";
                isPushOn = true;
            }

            if (isPushOn) {
                Intent intentPending = new Intent(context, Login.class);

                // 데이터 넘기기
                intentPending.putExtra("DATE", "2019-06-05");

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intentPending.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_UPDATE_CURRENT);

                callNotification(context, title, text, tickerText, pendingIntent, NOTIFICATION_CHANGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callNotification(Context context, String title, String text, String tickerText, PendingIntent pendingIntent, int notificationID) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, ClsNotificationChannel.CHANNEL_ID_PUSH)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setTicker(tickerText)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                        .setAutoCancel(true).setWhen(System.currentTimeMillis())
                        .setSound(defaultSoundUri)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);

        //Android 8.0이상에서 알림채널을 설정해야만 알림이 표시됨
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            notificationBuilder.setChannelId(ClsNotificationChannel.CHANNEL_ID_PUSH);

        notificationManager.notify(notificationID, notificationBuilder.build());
    }
}
