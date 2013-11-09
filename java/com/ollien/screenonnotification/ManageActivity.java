package com.ollien.screenonnotification;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ManageActivity extends Activity {

    private ListView lv;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> blockedList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        lv = (ListView)findViewById(R.id.blockedApps);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,blockedList);
        FileInputStream fis=null;
        try {
            fis = openFileInput("blockedApps.txt");
        } catch (IOException e) {
            Toast.makeText(ManageActivity.this, "You have no blocked apps to manage!", Toast.LENGTH_SHORT).show();
            finish();
//            e.printStackTrace();
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(fis));
        }
        catch (NullPointerException e) {
            Toast.makeText(ManageActivity.this, "You have no blocked apps to manage!", Toast.LENGTH_SHORT).show();
            finish();
        }
        String line;
        try {
            while ((line=reader.readLine())!=null){
                blockedList.add(line);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            Toast.makeText(ManageActivity.this, "You have no blocked apps to manage!", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (blockedList.size()==0){
            Toast.makeText(ManageActivity.this, "You have no blocked apps to manage!", Toast.LENGTH_SHORT).show();
            finish();
        }
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                unblockApp(blockedList.get(position));
                blockedList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void unblockApp(String packageName){
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("blockedApps.txt",MODE_PRIVATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            for (String name : blockedList){
                if (!name.equals(packageName)){
                    fos.write(name.getBytes());
                    fos.write("\n".getBytes());
                }
            }
            fos.close();
            Toast.makeText(ManageActivity.this,"Unblocked "+packageName,Toast.LENGTH_SHORT).show();
        }
        catch (NullPointerException e){
            Toast.makeText(ManageActivity.this, "NPE on writing the blocked apps to file.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.manage, menu);
        return true;
    }

    
}
