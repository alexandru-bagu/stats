package ow.stats;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        EditText et = (EditText)findViewById(R.id.ow_battletag);
        et.setText("Dad-12262");

        Button button = (Button)findViewById(R.id.ow_search_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent();
            }
        });
    }

    private void launchIntent() {
        Intent intent = new Intent(this, LoadingActivity.class);
        EditText et = (EditText)findViewById(R.id.ow_battletag);
        String battleTag = et.getText().toString();
        battleTag = battleTag.replace('#','-');
        intent.putExtra("battleTag", battleTag);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == resultCode) {
            String battleTag = data.getStringExtra("battleTag");
            StatsProvider instance = StatsProvider.getInstance();

            Intent intent = new Intent(this, StatisticsActivity.class);
            intent.putExtra("battleTag", battleTag);
            startActivity(intent);
        }
    }
}
