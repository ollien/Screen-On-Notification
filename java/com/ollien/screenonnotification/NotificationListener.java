package com.ollien.screenonnotification;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Nick on 11/8/13.
 */
public class NotificationListener extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
//        Intent i = new Intent(this,WakelockStarter.class);
//        sendBroadcast(i);
        Intent i = new Intent (this, BlockedAppReceiver.class);
        i.putExtra("package",sbn.getPackageName());
        sendBroadcast(i);
        FileInputStream fis = null;
        try {
            fis = openFileInput("blockedApps.txt");
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fos = openFileOutput("blockedApps.txt",Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        boolean fail=false;
        try {
            while ((line=reader.readLine())!=null){
                if (sbn.getPackageName().equals(line)){
                    fail=true;
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!fail){
            System.out.println("posted");
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    //        System.out.println("System Service aquired");
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,"ScreenOnNotification");
    //        System.out.println("wakelock created");
            wakeLock.acquire(1);
    //        System.out.println("Wakelocked!");
           PowerManager.WakeLock screenOffWakeLock  = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"ScreenOffNotification");
            screenOffWakeLock.acquire();
            screenOffWakeLock.release();


        }
        else{
            System.out.println("A blocked app sent a notification");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}
