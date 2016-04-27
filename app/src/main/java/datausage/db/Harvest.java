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
    public static final String KEY_SYNC_STATUS = "sync_status";

    public static final class SYNC {
        public static final int SYNCED = 1;
        public static final int YET_TO_BE_SYNCED = 0;
    }

    @Column(name = KEY_DATE, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String date;

    @Column(name = KEY_DATA)
    public String data;

    @Column(name = KEY_SYNC_STATUS)
    public int syncStatus;


}
