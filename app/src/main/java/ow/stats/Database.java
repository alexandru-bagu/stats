package ow.stats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

/**
 * Created by Sorin on 3/27/2017.
 */

public class Database extends SQLiteOpenHelper {

    private static Database _instance;

    public static Database getInstance() {
        return _instance;
    }
    public static void initInstance(Context context) {
        _instance = new Database(context);
    }

    public Database(Context context) {
        super(context, DatabaseSettings.DB_NAME, null, DatabaseSettings.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + DatabaseSettings.OwEntry.TABLE + " (" + DatabaseSettings.OwEntry.COL_TASK_BATTLETAG + " VARCHAR, " + DatabaseSettings.OwEntry.COL_TASK_JSON + " TEXT)";
        System.out.println("Create table " + createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseSettings.OwEntry.TABLE);
        onCreate(db);
    }

    public void deleteDatabase() {
        String deleteScript = "delete from " + DatabaseSettings.OwEntry.TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteScript);
    }

    public void insert(String battleTag, String json){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSettings.OwEntry.COL_TASK_BATTLETAG, battleTag);
        values.put(DatabaseSettings.OwEntry.COL_TASK_JSON, json);

        db.insertWithOnConflict(DatabaseSettings.OwEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();

    }

    public void update(String battleTag, String json) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseSettings.OwEntry.TABLE + " WHERE " + DatabaseSettings.OwEntry.COL_TASK_BATTLETAG + " = " + "'" + battleTag + "'", null);

        int battleIndex = c.getColumnIndex(DatabaseSettings.OwEntry.COL_TASK_BATTLETAG);

        if (c != null ) {
            if(c.moveToFirst()) {
                do {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseSettings.OwEntry.COL_TASK_BATTLETAG, c.getString(battleIndex));
                    values.put(DatabaseSettings.OwEntry.COL_TASK_JSON, json);

                    db.update(DatabaseSettings.OwEntry.TABLE, values, DatabaseSettings.OwEntry.COL_TASK_BATTLETAG + " = ?",
                            new String[]{String.valueOf(c.getString(battleIndex))});


                } while (c.moveToNext());
            }
        }
        c.close();
    }

    public String readJson(String battleTag) {
        String ret = null;
        SQLiteDatabase db = this.getWritableDatabase();

        String queryString = "SELECT json FROM " + DatabaseSettings.OwEntry.TABLE + " WHERE " + DatabaseSettings.OwEntry.COL_TASK_BATTLETAG + "=" + "'" + battleTag + "'";
        Cursor c = db.rawQuery(queryString, null);
        if(c != null)
        {
            if(c.moveToFirst())
            {
                int jsonIndex = c.getColumnIndex(DatabaseSettings.OwEntry.COL_TASK_JSON);
                ret = c.getString(jsonIndex);
            }
            c.close();
        }
        return ret;
    }
}
