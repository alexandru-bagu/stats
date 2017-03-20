package ow.stats;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Intent intent = getIntent();
        String battleTag = intent.getStringExtra("battleTag");
        StatsProvider instance = StatsProvider.getInstance();
        Cache cache = instance.getCache(battleTag);

        ImageView view = (ImageView)findViewById(R.id.ow_avatar);
        view.setImageBitmap(cache.getAvatar());
    }
}
