package datausage.rest;

import com.activeandroid.annotation.Column;

/**
 * Created by amal.chandran on 26/04/16.
 */
public class AppUsage {
    private String appPackageName;
    private String appName;
    private long appDateUseToday;

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppDateUseToday() {
        return appDateUseToday;
    }

    public void setAppDateUseToday(long appDateUseToday) {
        this.appDateUseToday = appDateUseToday;
    }
}
