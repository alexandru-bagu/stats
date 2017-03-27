package ow.stats;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class Cache {
    private String _battleTag;
    private Date _storageDate;
    private String _blob;
    private String _avatarBase64;

    public Cache(String battleTag) {
        _battleTag = battleTag;
    }

    public void setData(String blob, Bitmap avatar) {
        _blob = blob;
        _storageDate =  new Date();
        generateBase64(avatar);
    }

    private void generateBase64(Bitmap bmp)
    {
        if(bmp == null) {
            _avatarBase64 = null;
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        _avatarBase64 = Base64.encodeToString(b, Base64.DEFAULT);
    }

    public String getBattleTag() { return _battleTag; }
    public Date getStorageDate() {
        return _storageDate;
    }

    public Bitmap getAvatar() {
        if(_avatarBase64 == null) return null;
        byte [] encodeByte=Base64.decode(_avatarBase64,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;
    }

    public JSONObject getJSON() {
        JSONObject _object = null;
        try {
            _object = new JSONObject(_blob);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return _object;
    }
}
