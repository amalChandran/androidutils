package datausage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Created by amal.chandran on 21/04/16.
 */

public interface SubscribeForNetworkStats {
    void networkUpdates(List<ApplicationItem> mApplicationItemList);
}
