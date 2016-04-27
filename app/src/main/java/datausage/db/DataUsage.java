package datausage.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


/**
 * Created by amal.chandran on 26/04/16.
 */
@Table(name = "data_usage")
public class DataUsage extends Model {

    public static final String KEY_APP_PACKAGE = "app_package";
    public static final String KEY_APP_NAME = "app_name";
    public static final String KEY_DATA_TODAY = "app_data_today";
    public static final String APP_DATA_START = "app_data_start";
    public static final String KEY_LAST_UPDATED = "last_updated";

    @Column(name = KEY_APP_PACKAGE, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String appPackageName;

    @Column(name = KEY_APP_NAME)
    public String appName;

    @Column(name = KEY_DATA_TODAY)
    public long appDateUseToday;

    @Column(name = APP_DATA_START)
    public long appDataStart;

    @Column(name = KEY_LAST_UPDATED)
    public long lastUpdatedAt;

}
