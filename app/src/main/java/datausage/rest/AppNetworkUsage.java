package datausage.rest;

/**
 * Created by amal.chandran on 26/04/16.
 */
public class AppNetworkUsage {
    private String appPackageName;
    private String appName;
    private long dataConsumed;

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

    public long getDataConsumed() {
        return dataConsumed;
    }

    public void setDataConsumed(long dataConsumed) {
        this.dataConsumed = dataConsumed;
    }
}
