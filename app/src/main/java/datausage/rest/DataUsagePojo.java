package datausage.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amal.chandran on 26/04/16.
 */
public class DataUsagePojo {
    private String totalDataUsage;
    private String date;
    private long timeOn3g;
    private long timeOn2G;
    private long timeDisconnected;
    private boolean isRooted;
    private AppBatteryUsage appBatteryUsage;
    private List<AppNetworkUsage> appUsages = new ArrayList<AppNetworkUsage>();

    public String getTotalDataUsage() {
        return totalDataUsage;
    }

    public void setTotalDataUsage(String totalDataUsage) {
        this.totalDataUsage = totalDataUsage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<AppNetworkUsage> getAppUsages() {
        return appUsages;
    }

    public void setAppUsages(List<AppNetworkUsage> appUsages) {
        this.appUsages = appUsages;
    }


    public long getTimeOn3g() {
        return timeOn3g;
    }

    public void setTimeOn3g(long timeOn3g) {
        this.timeOn3g = timeOn3g;
    }

    public long getTimeOn2G() {
        return timeOn2G;
    }

    public void setTimeOn2G(long timeOn2G) {
        this.timeOn2G = timeOn2G;
    }

    public long getTimeDisconnected() {
        return timeDisconnected;
    }

    public void setTimeDisconnected(long timeDisconnected) {
        this.timeDisconnected = timeDisconnected;
    }

    public boolean isRooted() {
        return isRooted;
    }

    public void setRooted(boolean rooted) {
        isRooted = rooted;
    }

    public AppBatteryUsage getAppBatteryUsage() {
        return appBatteryUsage;
    }

    public void setAppBatteryUsage(AppBatteryUsage appBatteryUsage) {
        this.appBatteryUsage = appBatteryUsage;
    }
}
