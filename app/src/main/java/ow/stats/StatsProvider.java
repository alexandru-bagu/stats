package ow.stats;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StatsProvider {

    private static CacheStorage _storageInstance;
    private static StatsProvider _instance;
    private CacheStorage _storage;

    public static StatsProvider getInstance() {
        if(_instance == null) {
            _storageInstance = new CacheStorage();
            _instance = new StatsProvider(_storageInstance);
        }
        return _instance;
    }

    public static Long getDifference(Date date1, Date date2, TimeUnit timeUnit) {
        Long milliseconds = date2.getTime() - date1.getTime();
        return timeUnit.convert(milliseconds, TimeUnit.MILLISECONDS);
    }

    public StatsProvider(CacheStorage storage) {
        _storage = storage;
    }

    public void saveCache(Cache cache) {
        _storage.saveCache(cache);
    }

    public Cache getCache(String battleTag) {
        Cache cache = _storage.getCache(battleTag);
        return cache;
    }

    public Cache generateCache(String battleTag, String blob, Bitmap avatar) {
        Cache cache = new Cache(battleTag);
        cache.setData(blob, avatar);
        _storage.saveCache(cache);
        return cache;
    }

    public CacheStatus getCacheStatus(String battleTag) {
        Cache cache = _storage.getCache(battleTag);
        if(cache != null) {
            Date date = cache.getStorageDate();
            Long diff = getDifference(date, new Date(), TimeUnit.HOURS);
            if(Math.abs(diff) < 2) {
                return CacheStatus.Available;
            }
            else {
                return CacheStatus.Outdated;
            }
        } else {
            return CacheStatus.Unavailable;
        }
    }

    public List<String> getAvailableRegions(JSONObject json) {
        List<String> list = new ArrayList<String>();

        try {
            if(json.has("kr") && (json.getString("kr") != "null"))
                list.add("kr");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if(json.has("eu") && (json.getString("eu") != "null"))
                list.add("eu");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if(json.has("us") && (json.getString("us") != "null"))
                list.add("us");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getAvatar(JSONObject json) {

        for(String region : getAvailableRegions(json)) {
            try {
                JSONObject obj = json.getJSONObject(region);
                obj = obj.getJSONObject("stats");
                obj = obj.getJSONObject("quickplay");
                obj = obj.getJSONObject("overall_stats");
                return obj.getString("avatar");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
