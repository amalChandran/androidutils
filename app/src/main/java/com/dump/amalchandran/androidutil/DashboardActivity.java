package com.dump.amalchandran.androidutil;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import datausage.AppNetUsage;
import datausage.ApplicationItem;
import datausage.NetworkUtil;
import datausage.SubscribeForNetworkStats;

public class DashboardActivity extends AppCompatActivity {

    AppNetUsage appNetUsage;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        appNetUsage = new AppNetUsage(getApplicationContext(), new SubscribeForNetworkStats() {
            @Override
            public void networkUpdates(List<ApplicationItem> mApplicationItemList) {
                Log.i("Network " , "=====================================================================================");
                Log.i("Network " , "====================================================================================="+mApplicationItemList.size());
                for (ApplicationItem item : mApplicationItemList) {
                    Log.i("Network ", "" + item.getApplicationPackage() + " : " + item.getTotalUsageKb());
                }

                Log.i("Net type : ", "net type : "+ NetworkUtil.getNetworkType(getApplicationContext()));
            }
        }) ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        appNetUsage.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        appNetUsage.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
