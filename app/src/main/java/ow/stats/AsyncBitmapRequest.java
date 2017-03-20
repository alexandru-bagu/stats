package ow.stats;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncBitmapRequest extends AsyncTask {
    private URL _url;
    private Boolean _urlIsValid;

    public AsyncBitmapRequest(String url) {
        try {
            _url = new URL(url);
            _urlIsValid = true;
        } catch (MalformedURLException e) {
            _urlIsValid = false;
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        if(!_urlIsValid) return "";
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) _url.openConnection();
            int code = connection.getResponseCode();
            if(code == 200) {
                InputStream stream = connection.getInputStream();
                return BitmapFactory.decodeStream(stream);
            }
        } catch (IOException e) {
        }  finally {
            connection.disconnect();
        }
        return "";
    }
}