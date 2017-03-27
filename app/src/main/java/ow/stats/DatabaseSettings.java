package ow.stats;

/**
 * Created by Sorin on 3/27/2017.
 */

import android.provider.BaseColumns;

/**
 * Created by benwong on 2016-07-10.
 */
public class DatabaseSettings {
    public static final String DB_NAME = "ow.stats.db";
    public static final int DB_VERSION = 1;

    public class OwEntry implements BaseColumns {
        public static final String TABLE = "data";

        public static final String COL_TASK_BATTLETAG = "battleTag";
        public static final String COL_TASK_JSON = "json";
    }
}
