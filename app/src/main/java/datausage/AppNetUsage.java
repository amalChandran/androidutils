package datausage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import datausage.db.NetworkDbUtils;
import datausage.db.Harvest;

public class AppNetUsage {

    private Context mContext;
    private Timer mTimer;
    private TimerTask mTask;

    private int TIME_APPLICATION_UPDATE = 3 * 1000; // 3 second

    private boolean isWifiEnabled = false;
    private boolean isMobilEnabled = false;

    private List<ApplicationItem> mApplicationItemList = new ArrayList<ApplicationItem>();

    private SubscribeForNetworkStats subscribeForNetworkStats;

    public AppNetUsage(Context _context, SubscribeForNetworkStats subscribeForNetworkStats) {
        if (subscribeForNetworkStats == null) {
            throw new NullPointerException("SubscribeForNetworkStats listener cannot be null.");
        }

        if(_context == null){
            throw new NullPointerException("Context cannot be null");
        }

        this.subscribeForNetworkStats = subscribeForNetworkStats;
        mContext = _context;
        updateNetworkState();

    }

    public void start() {
        stop();

        mTask = new TimerTask() {
            @Override
            public void run() {
                update();
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 0, TIME_APPLICATION_UPDATE);
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }

    public void update() {
        updateNetworkState();
        if(mApplicationItemList.size() > 0) {
            for (int i = 0, l = mApplicationItemList.size(); i < l; i++) {
                mApplicationItemList.get(i).setMobilTraffic(isMobilEnabled);
                mApplicationItemList.get(i).update();
            }
        } else {
            for (ApplicationInfo app : mContext.getPackageManager().getInstalledApplications(0)) {
                ApplicationItem item = new ApplicationItem(app, mContext);
                item.setMobilTraffic(isMobilEnabled);

                mApplicationItemList.add(item);
            }
        }

        Collections.sort(mApplicationItemList, new Comparator < ApplicationItem > () {
            @Override
            public int compare (ApplicationItem lhs, ApplicationItem rhs){
                return (int) (rhs.getTotalUsageKb() - lhs.getTotalUsageKb());
            }
        });


        //Update the Datausage DB
        ArrayList<ApplicationItem> applicationItems = new ArrayList<ApplicationItem>(mApplicationItemList);
        NetworkDbUtils.getInstance(mContext).saveValuesToDB(applicationItems);
        //Update the Harvest DB.
        recordNetworkTime();

        //TODO: Push the data to subscribers.
        subscribeForNetworkStats.networkUpdates(mApplicationItemList);

    }

    /**  Duration of network types used. **/
    private void recordNetworkTime(){
        Harvest harvest = new Select().from(Harvest.class).where(Harvest.KEY_DATE + " = ?", getDateFromMillis(System.currentTimeMillis())).executeSingle();
        if (harvest == null) {
            harvest = new Harvest();
            harvest.syncStatus = Harvest.SYNC.NOT_READY_FOR_SYNCED;
            harvest.date = getDateFromMillis(System.currentTimeMillis());
        }

        String networkType = NetworkUtil.getNetworkType(mContext);
        if(networkType.equalsIgnoreCase("3g")){
            harvest.timeOn3G = harvest.timeOn3G + (TIME_APPLICATION_UPDATE/1000);
        } else if (networkType.equalsIgnoreCase("2g")){
            harvest.timeOn2G = harvest.timeOn2G + (TIME_APPLICATION_UPDATE/1000);
        } else {
            //Disconnected
            harvest.timeDisconnected = harvest.timeDisconnected + (TIME_APPLICATION_UPDATE/1000);

        }

        harvest.save();
    }



    private void updateNetworkState() {
        isWifiEnabled = isConnectedWifi();
        isMobilEnabled = isConnectedMobile();
    }

    public List<ApplicationItem> getList() {
        return mApplicationItemList;
    }

    public boolean isConnectedWifi(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public boolean isConnectedMobile(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    private String getDateFromMillis(long millis) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

        return format1.format(cal.getTime());
    }

}
