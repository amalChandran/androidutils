package datausage.db;

import android.util.Log;

import com.activeandroid.query.Select;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import datausage.ApplicationItem;
import datausage.rest.AppUsage;
import datausage.rest.DataUsagePojo;

/**
 * Created by amal.chandran on 26/04/16.
 */
public class DbUtils {

    private static DbUtils DBUTILS;

    private DbUtils() {

    }

    public static DbUtils getInstance() {
        if (DBUTILS == null) {
            DBUTILS = new DbUtils();
        }

        return DBUTILS;
    }

    //TODO: Compare and save to db.
    public void saveValuesToDB(ArrayList<ApplicationItem> mApplicationItemList) {

        boolean isDailyRefreshRequired = false;

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

    private void harvestData() {
        //Query the table and collate it into json data and add to the next db
        //Update the lastUpdatedAt and appDataStart to the current value.

        DataUsagePojo dataUsagePojo = new DataUsagePojo();
        List<AppUsage> appUsages = new ArrayList<AppUsage>();
        long totalAppUsage = 0;
        long lastUpdated = 0;

        List<DataUsage> dataUsages = new Select()
                .from(DataUsage.class)
                .execute();
        for (DataUsage usage : dataUsages) {

            lastUpdated = usage.lastUpdatedAt;
            totalAppUsage = totalAppUsage + usage.appDateUseToday;

            AppUsage appUsage = new AppUsage();
            appUsage.setAppDateUseToday(usage.appDateUseToday);
            appUsage.setAppName(usage.appName);
            appUsage.setAppPackageName(usage.appPackageName);
            appUsages.add(appUsage);

            //Updating the existing table values
//            usage.appDataStart = usage.appDateUseToday + usage.appDataStart;
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

        Harvest harvest = new Harvest();
        harvest.date = dataUsagePojo.getDate();
        harvest.syncStatus = Harvest.SYNC.YET_TO_BE_SYNCED;

        Gson gson = new Gson();
        String data = gson.toJson(dataUsagePojo);
        Log.i("Harvested data", data);
        harvest.data = data;

        harvest.save();
    }


    //UTILS

    private boolean isLastUpdatedToday(long timeStamp) {
//        final Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(timeStamp);
//        Date lastUpdatedDate = cal.getTime();
//
//        final Calendar cal2 = Calendar.getInstance();
//        cal.setTimeInMillis(System.currentTimeMillis());
//        Date todaysDate = cal.getTime();

//        int result = lastUpdatedDate.compareTo(todaysDate);

        String lastUpdatedDate = getDateFromMillis(timeStamp);
        String currentDate = getDateFromMillis(System.currentTimeMillis());

//        Log.i("Date", lastUpdatedDate + "||" + currentDate);

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

}
