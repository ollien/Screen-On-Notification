package com.ollien.screenonnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by Nick on 11/8/13.
 */
public class BlockedAppReceiver extends BroadcastReceiver {
    public static ArrayList<String> packages= new ArrayList<String>();
    public void onReceive(Context context, Intent intent) {

        if (!packages.contains(intent.getStringExtra("package"))){
            packages.add(intent.getStringExtra("package"));
        }
    }
}
