package ow.stats;

import java.util.HashMap;
import java.util.Map;

public class CacheStorage {

    private Map<String, Cache> _cache = new HashMap<String, Cache>();

    public Cache getCache(String battleTag) {
        if(_cache.containsKey(battleTag)) {
            return _cache.get(battleTag);
        }
        return null;
    }

    public void saveCache(Cache cache) {
        String battleTag = cache.getBattleTag();
        _cache.remove(battleTag);
        _cache.put(battleTag, cache);
    }
}
