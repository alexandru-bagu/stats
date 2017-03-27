package ow.stats;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class CacheStorage {

    private Map<String, Cache> _cache = new HashMap<String, Cache>();

    public Cache getCache(String battleTag) {
        if(_cache.containsKey(battleTag)) {
            return _cache.get(battleTag);
        }
        Database db = Database.getInstance();
        String json = db.readJson(battleTag);
        if(json != null) {
            Gson gson = new Gson();
            Cache cache = gson.fromJson(json, Cache.class);
            saveCache(cache, false);
            return cache;
        }
        return null;
    }

    public void saveCache(Cache cache) {
        saveCache(cache, true);
    }

    public void saveCache(Cache cache, Boolean save) {
        String battleTag = cache.getBattleTag();

        if(save) {
            Gson gson = new Gson();
            String json = gson.toJson(cache);

            Database db = Database.getInstance();
            if (db.readJson(battleTag) == null)
                db.insert(battleTag, json);
            else
                db.update(battleTag, json);
        }
        if(_cache.containsKey(battleTag))
            _cache.remove(battleTag);
        _cache.put(battleTag, cache);
    }
}
