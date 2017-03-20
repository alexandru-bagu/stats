package ow.stats;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncStringRequest extends AsyncTask {
    private URL _url;
    private Boolean _urlIsValid;

    public AsyncStringRequest(String url) {
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer buffer = new StringBuffer();
                String line;
                while((line = reader.readLine()) != null)
                    buffer.append(line);
                return buffer.toString();
            }
        } catch (IOException e) {
        }  finally {
            connection.disconnect();
        }
        return "";
    }
}