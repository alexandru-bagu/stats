package ow.stats;

import android.media.Image;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;

public class Cache {
    private String _battleTag;
    private Date _storageDate;
    private String _blob;
    private Image _avatar;
    private JSONObject _object;

    public Cache(String battleTag) {
        _battleTag = battleTag;
    }

    public void setData(String blob, Image avatar) throws JSONException {
        _blob = blob;
        _avatar = avatar;
        _storageDate =  new Date();
        _object = new JSONObject(_blob);
    }

    public String getBlob() {
        return _blob;
    }
    public Date getStorageDate() {
        return _storageDate;
    }
    public Image getAvatar() {
        return _avatar;
    }
    public JSONObject getJSON() { return _object; }
}
