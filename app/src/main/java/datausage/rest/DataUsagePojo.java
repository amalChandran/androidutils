package datausage.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amal.chandran on 26/04/16.
 */
public class DataUsagePojo {
    private String totalDataUsage;
    private String date;
    private List<AppUsage> appUsages = new ArrayList<AppUsage>();

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

    public List<AppUsage> getAppUsages() {
        return appUsages;
    }

    public void setAppUsages(List<AppUsage> appUsages) {
        this.appUsages = appUsages;
    }
}
