package ow.stats;

import android.content.Context;
import android.content.Intent;
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
                AsyncTask task = new AsyncHttpRequest("https://owapi.net/api/v3/u/" + battleTag + "/blob") {
                    @Override
                    protected void onPostExecute(Object o) {
                        String json = o.toString();
                        processOutput(battleTag, json);
                    }
                };
                task.execute();

        }
        showDetails(instance.getCache(battleTag));
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void processOutput(String battleTag, String json) {
        if(json == "") {
            showToast("Cannot get data from the internet. Please try again later.");
            finish();
            return;
        }

        JSONObject data = null;
        StatsProvider instance = StatsProvider.getInstance();
        try {
            data = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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



        Cache cache = instance.generateCache(battleTag, json, data);
        showDetails(cache);
    }

    private void showDetails(Cache cache) {

    }


}
