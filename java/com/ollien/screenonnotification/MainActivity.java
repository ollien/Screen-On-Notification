package com.ollien.screenonnotification;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private EditText packageEditText;
    private ListView recentList;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        packageEditText=(EditText)findViewById(R.id.addBlockedApp);
        recentList = (ListView)findViewById(R.id.recentApps);
        adapter  = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,BlockedAppReceiver.packages);
        recentList.setAdapter(adapter);
        recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                blockApp(BlockedAppReceiver.packages.get(position));
            }
        });
        SharedPreferences settings = getSharedPreferences("SETTINGS",MODE_PRIVATE);
            if (settings.getBoolean("firstRun",true)){
                AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(this);
                confirmDialogBuilder.setMessage("Add recommended blocked Apps? These are Google Music (Song changes would turn on the screen), System, and System UI").setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        blockApp("android");
                        blockApp("com.android.systemui");
                        blockApp("com.google.android.music");
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog confirmDialog = confirmDialogBuilder.create();
                confirmDialog.show();
                settings.edit().putBoolean("firstRun",true).commit();
            }
        }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void waitAndPost(View v){
        new Thread(){
            public void run(){

            try {
                Thread.sleep(2000);
                NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder ncomp = new Notification.Builder(MainActivity.this);
                ncomp.setContentTitle("My Notification");
                ncomp.setContentText("Notification Listener Service Example");
                ncomp.setTicker("Notification Listener Service Example");
                ncomp.setSmallIcon(R.drawable.ic_launcher);
                ncomp.setAutoCancel(true);
                nManager.notify((int)System.currentTimeMillis(),ncomp.build());
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }.start();
    }
    public void blockFromEditText(View v){
        blockApp(packageEditText.getText().toString());
    }
    protected void blockApp(String packageName){

        FileOutputStream fos=null;
        try {
            fos  = openFileOutput("blockedApps.txt",Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                fos = openFileOutput("blockedApps.txt",Context.MODE_PRIVATE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try{
            fos.write(packageName.getBytes());
            fos.write("\n".getBytes());
            fos.close();
            Toast.makeText(MainActivity.this, "Blocked "+packageName,Toast.LENGTH_SHORT).show();

        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "NPE on writing the blocked app to file.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==R.id.manage){
            Intent i = new Intent(this,ManageActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

}

