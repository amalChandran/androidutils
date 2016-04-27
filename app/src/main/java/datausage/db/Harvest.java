package datausage.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


/**
 * Created by amal.chandran on 26/04/16.
 */
@Table(name = "harvest")
public class Harvest extends Model {

    public static final String KEY_DATE = "date";
    public static final String KEY_DATA = "data_json";
    public static final String KEY_TIME_3G = "time_on_3g";
    public static final String KEY_TIME_2G = "time_on2g";
    public static final String KEY_TIME_DISCONNECTED = "time_disconnected";
    public static final String KEY_SYNC_STATUS = "sync_status";

    public static final class SYNC {
        public static final int READY_FOR_SYNC = 1;
        public static final int NOT_READY_FOR_SYNCED = 0;
    }

    @Column(name = KEY_DATE, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String date;

    @Column(name = KEY_DATA)
    public String data;

    @Column(name = KEY_TIME_3G)
    public long timeOn3G;

    @Column(name = KEY_TIME_2G)
    public long timeOn2G;

    @Column(name = KEY_TIME_DISCONNECTED)
    public long timeDisconnected;

    @Column(name = KEY_SYNC_STATUS)
    public int syncStatus;


}
