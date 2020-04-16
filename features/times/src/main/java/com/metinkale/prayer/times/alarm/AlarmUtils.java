/*
 * Copyright (c) 2013-2019 Metin Kale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metinkale.prayer.times.alarm;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.metinkale.prayer.base.BuildConfig;
import com.metinkale.prayer.times.R;
import com.metinkale.prayer.times.fragments.TimesFragment;
import com.metinkale.prayer.times.times.Times;
import com.metinkale.prayer.times.utils.NotificationUtils;

public class AlarmUtils {


    public static Notification buildAlarmNotification(Context c, Alarm alarm, long time) {
        Times t = alarm.getCity();
        String text = t.getName() + " (" + t.getSource() + ")";

        String txt = alarm.getCurrentTitle();

        if (BuildConfig.DEBUG) {
            long difference = System.currentTimeMillis() - time;
            if (difference < 5000) {
                txt += " " + difference + "ms";
            } else if (difference < 3 * 60 * 1000) {
                txt += " " + difference / 1000 + "s";
            } else {
                txt += " " + difference / 1000 / 60 + "m";
            }
        }

        androidx.core.app.NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new androidx.core.app.NotificationCompat.Builder(c, NotificationUtils.getAlarmChannel(c));
        } else {
            builder = new NotificationCompat.Builder(c);
        }

        builder = builder.setContentTitle(text)
                .setContentText(txt)
                .setContentIntent(TimesFragment.getPendingIntent(t))
                .setSmallIcon(R.drawable.ic_abicon)
                .setWhen(time);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NotificationUtils.getAlarmChannel(c));
        } else {
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        }
        return builder.build();
    }

    public static Notification buildPlayingNotification(Context c, Alarm alarm, long time) {
        Times t = alarm.getCity();
        String text = t.getName() + " (" + t.getSource() + ")";

        String txt = alarm.getCurrentTitle();

        if (BuildConfig.DEBUG) {
            long difference = System.currentTimeMillis() - time;
            if (difference < 5000) {
                txt += " " + difference + "ms";
            } else if (difference < 3 * 60 * 1000) {
                txt += " " + difference / 1000 + "s";
            } else {
                txt += " " + difference / 1000 / 60 + "m";
            }
        }

        PendingIntent stopIndent = PendingIntent.getBroadcast(c, 0, new Intent(c, AlarmService.StopAlarmPlayerReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(c, NotificationUtils.getAlarmChannel(c));
        } else {
            builder = new NotificationCompat.Builder(c);
        }

        builder = builder.setContentTitle(text)
                .setContentText(txt)
                .setContentIntent(TimesFragment.getPendingIntent(t))
                .setSmallIcon(R.drawable.ic_abicon)
                .setOngoing(true)
                .addAction(R.drawable.ic_action_stop, c.getString(R.string.stop), stopIndent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0))
                .setWhen(time);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NotificationUtils.getAlarmChannel(c));
        } else {
            builder.setPriority(Notification.PRIORITY_MAX);
        }
        return builder.build();
    }
}
