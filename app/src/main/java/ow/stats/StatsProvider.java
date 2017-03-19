package ow.stats;

import org.json.JSONObject;

import java.util.Date;
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

    public Cache getCache(String battleTag) {
        Cache cache = _storage.getCache(battleTag);
        return cache;
    }

    public Cache generateCache(String battleTag, String blob, JSONObject data) {
        Cache cache = new Cache(battleTag);
        //cache.setData(blob, )
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

    public String getAvailableRegions(Cache cache) {
        return "";
    }

}
