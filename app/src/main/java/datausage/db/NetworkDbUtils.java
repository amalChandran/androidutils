package datausage.db;

import android.content.Context;
import android.util.Log;

import com.activeandroid.query.Select;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import datausage.ApplicationItem;
import datausage.rest.AppBatteryUsage;
import datausage.rest.AppNetworkUsage;
import datausage.rest.DataUsagePojo;

/**
 * Created by amal.chandran on 26/04/16.
 */
public class NetworkDbUtils {

    private static NetworkDbUtils DBUTILS;

    private Context context;
    private NetworkDbUtils(Context context) {
        this.context = context;
    }

    public static NetworkDbUtils getInstance(Context context) {
        if (DBUTILS == null) {
            DBUTILS = new NetworkDbUtils(context);
        }

        return DBUTILS;
    }

    //TODO: Compare and save to db.
    public void saveValuesToDB(ArrayList<ApplicationItem> mApplicationItemList) {

        boolean isDailyRefreshRequired = false;

        /**  Measure apps data usage **/
        for (ApplicationItem applicationItem : mApplicationItemList) {
            Log.d("debug", applicationItem.getApplicationPackage());
            DataUsage dataUsage = new Select().from(DataUsage.class).where(DataUsage.KEY_APP_PACKAGE + " = ?", applicationItem.getApplicationPackage()).executeSingle();
            if (dataUsage == null) {
                DataUsage item = new DataUsage();
                item.appName = applicationItem.getApplicationLabel();
                item.appPackageName = applicationItem.getApplicationPackage();
                item.appDateUseToday = 0;
                item.appDataStart = applicationItem.getTotalUsageKb();
                item.lastUpdatedAt = System.currentTimeMillis();
                item.save();
            } else {

                if (isLastUpdatedToday(dataUsage.lastUpdatedAt)) {
                    //Continue updating table.
                    long dataUseToday = applicationItem.getTotalUsageKb() - dataUsage.appDataStart;
                    if(dataUseToday >= 0){
                        dataUsage.appDateUseToday = dataUsage.appDateUseToday + dataUseToday;
                    }

                    dataUsage.appDataStart = applicationItem.getTotalUsageKb();
                    dataUsage.lastUpdatedAt = System.currentTimeMillis();
                    dataUsage.save();
                } else {
                    isDailyRefreshRequired = true;
                    break;
                }
            }
        }

        if (isDailyRefreshRequired) {
            //Save data to the next db
            harvestData();
        }
    }

    private void pushDataToServer(){
        Log.d("pushDataToServer", "enter");
        ArrayList<DataUsagePojo> dataUsagePojos = new ArrayList<DataUsagePojo>();

        List<Harvest> harvests = new Select().from(Harvest.class).where(Harvest.KEY_SYNC_STATUS + " = ?",  Harvest.SYNC.READY_FOR_SYNC).execute();
        for (Harvest harvest : harvests) {

            Gson gson = new Gson();
            DataUsagePojo dataUsagePojo = gson.fromJson(harvest.data, DataUsagePojo.class);

            dataUsagePojo.setAppBatteryUsage(new AppBatteryUsage());
            dataUsagePojo.setRooted(false);
            dataUsagePojo.setTimeDisconnected(harvest.timeDisconnected);
            dataUsagePojo.setTimeOn2G(harvest.timeOn2G);
            dataUsagePojo.setTimeOn3g(harvest.timeOn3G);

            dataUsagePojos.add(dataUsagePojo);
        }

//        Log.i("Data for server", "Harvested data : " + new Gson().toJson(dataUsagePojos));
        extraLargeLog(new Gson().toJson(dataUsagePojos));
    }

    private void harvestData() {
        //Query the table and collate it into json data and add to the next db
        //Update the lastUpdatedAt and appDataStart to the current value.

        DataUsagePojo dataUsagePojo = new DataUsagePojo();
        List<AppNetworkUsage> appUsages = new ArrayList<AppNetworkUsage>();
        long totalAppUsage = 0;
        long lastUpdated = 0;

        List<DataUsage> dataUsages = new Select()
                .from(DataUsage.class)
                .execute();
        for (DataUsage usage : dataUsages) {

            lastUpdated = usage.lastUpdatedAt;
            totalAppUsage = totalAppUsage + usage.appDateUseToday;

            AppNetworkUsage appUsage = new AppNetworkUsage();
            appUsage.setDataConsumed(usage.appDateUseToday);
            appUsage.setAppName(usage.appName);
            appUsage.setAppPackageName(usage.appPackageName);
            appUsages.add(appUsage);

            //Updating the existing table values
            usage.appDateUseToday = 0;
            usage.lastUpdatedAt = System.currentTimeMillis();
            usage.save();
        }

        dataUsagePojo.setDate(getDateFromMillis(lastUpdated));
        dataUsagePojo.setTotalDataUsage("" + totalAppUsage);
        dataUsagePojo.setAppUsages(appUsages);

        Log.i("Harvested", "Data harvested : " + totalAppUsage);

        updateHarvestedData(dataUsagePojo);
    }

    private void updateHarvestedData(DataUsagePojo dataUsagePojo) {
        Harvest harvest = new Select().from(Harvest.class).where(Harvest.KEY_DATE + " = ?", dataUsagePojo.getDate()).executeSingle();
        if (harvest == null) {
            harvest = new Harvest();
            harvest.date = dataUsagePojo.getDate();
        }

        harvest.syncStatus = Harvest.SYNC.READY_FOR_SYNC;

        Gson gson = new Gson();
        String data = gson.toJson(dataUsagePojo);
        Log.i("Harvested data", data);
        harvest.data = data;

        harvest.save();

        pushDataToServer();
    }


    //UTILS

    private boolean isLastUpdatedToday(long timeStamp) {
        String lastUpdatedDate = getDateFromMillis(timeStamp);
        String currentDate = getDateFromMillis(System.currentTimeMillis());
        if (lastUpdatedDate.equals(currentDate)) {
            return true;
        } else {
            Log.i("Date", lastUpdatedDate + "||" + currentDate);
            return false;
        }
    }

    private String getDateFromMillis(long millis) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

        return format1.format(cal.getTime());
    }

    private void extraLargeLog(String veryLongString){
        int maxLogSize = 1000;
        for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            Log.v("Data Harvested", "Data harvested : "+ veryLongString.substring(start, end));
        }
    }

}
