package ow.stats;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class LoadingActivity extends AppCompatActivity {

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean online = false;
        for (NetworkInfo ni : connectivityManager.getAllNetworkInfo())
            online |= ni.isAvailable() && ni.isConnectedOrConnecting();
        return online;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent intent = getIntent();
        final String battleTag = intent.getStringExtra("battleTag");

        StatsProvider instance = StatsProvider.getInstance();
        CacheStatus status = instance.getCacheStatus(battleTag);

        if(status == CacheStatus.Unavailable && !isNetworkAvailable())
        {
            showToast("You require an internet connection to get statistics.");
            finish();
            return;
        }

        if(isNetworkAvailable() && (status == CacheStatus.Unavailable || status == CacheStatus.Outdated))
        {
            AsyncTask task = new AsyncStringRequest("https://owapi.net/api/v3/u/" + battleTag + "/blob") {
                @Override
                protected void onPostExecute(Object o) {
                    String json = o.toString();
                    processOutput(battleTag, json);
                }
            };
            task.execute();
            return;
        }
        showDetails(instance.getCache(battleTag));
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void processOutput(final String battleTag, final String json) {

        StatsProvider instance = StatsProvider.getInstance();

        if(json == "") {
            showToast("Cannot get data from the internet. Please try again later.");
            finish();
            return;
        }

        final JSONObject data = getJSONObject(json);


        if(data == null) {
            showToast("Cannot get data from the internet. Please try again later.");
            finish();
            return;
        }

        if(data.has("error")) {
            int error = 0;
            try {
                error = data.getInt("error");
            } catch (JSONException e) { }
            if (error == 404) {
                showToast("Profile could not be found.");
                finish();
                return;
            } else {
                showToast("Try again.");
                finish();
                return;
            }
        }

        String avatar = instance.getAvatar(data);

        try {
            URL url = new URL(avatar);
            url.toURI();


            AsyncTask task = new AsyncBitmapRequest(avatar) {
                @Override
                protected void onPostExecute(Object o) {
                    Bitmap bmp = (Bitmap)o;
                    processAvatar(battleTag, json, bmp);
                }
            };
            task.execute();
            return;

        } catch (MalformedURLException e) {
            Log.d("test", e.toString());

        } catch (URISyntaxException e) {
            Log.d("test", e.toString());
        }

        Cache cache = instance.generateCache(battleTag, json, null);
        instance.saveCache(cache);
        showDetails(cache);
    }

    public JSONObject getJSONObject(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processAvatar(String battleTag, String json, Bitmap avatar) {
        StatsProvider instance = StatsProvider.getInstance();
        Cache cache = instance.generateCache(battleTag, json, avatar);
        instance.saveCache(cache);
        showDetails(cache);
    }

    private void showDetails(Cache cache) {
        setResult(1, getIntent());
        finish();
    }
}
