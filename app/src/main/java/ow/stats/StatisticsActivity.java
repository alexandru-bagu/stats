package ow.stats;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    String _battleTag;
    Cache _cache;
    JSONObject _json;
    List<String> _regions;
    List<String> _types;
    List<String> _fullNameRegions;
    List<String> _fullNameTypes;

    String _type = "quickplay";
    String _region = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        StatsProvider instance = StatsProvider.getInstance();
        Intent intent = getIntent();

        _battleTag = intent.getStringExtra("battleTag");
        _cache = instance.getCache(_battleTag);
        _json = _cache.getJSON();
        _regions = instance.getAvailableRegions(_json);

        setTitle(getTitle() + " - " + _battleTag);

        ImageView view = (ImageView)findViewById(R.id.ow_avatar);
        Bitmap avatar = _cache.getAvatar();
        view.setImageBitmap(avatar);

        _fullNameRegions = getFullNameRegions(_regions);
        _region = _regions.get(0);
        _types = new ArrayList<String>();
        _types.add("quickplay");
        _types.add("competitive");

        _fullNameTypes = new ArrayList<String>();
        _fullNameTypes.add("Quick-Play");
        _fullNameTypes.add("Competitive");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_itemview, _fullNameRegions);
        Spinner regions = (Spinner) findViewById(R.id.spinner_region);
        regions.setAdapter(adapter);
        regions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _region = _regions.get(position);
                showStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_itemview, _fullNameTypes);
        Spinner types = (Spinner) findViewById(R.id.spinner_type);
        types.setAdapter(adapter2);
        types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _type = _types.get(position);
                showStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showStats();
    }

    private ArrayList<String> getFullNameRegions(List<String> regions) {
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < regions.size(); i++) {
            if(regions.get(i) == "us") list.add("United States");
            else if(regions.get(i) == "eu") list.add("Europe");
            else if(regions.get(i) == "kr") list.add("Korea");
            else list.add(regions.get(i));
        }
        return list;
    }

    private String getString(JSONObject obj, String key) {
        try {
            return obj.getString(key);
        } catch (JSONException e) {
            return "0";
        }
    }

    private void showStats() {
        try {
            if (_regions.size() > 0) {
                JSONObject obj = _json.getJSONObject(_region);
                obj = obj.getJSONObject("stats");
                JSONObject type = obj.getJSONObject(_type);
                obj = type.getJSONObject("overall_stats");

                String tier = getString(obj, "tier");
                String games = getString(obj, "games");
                String win_rate = getString(obj, "win_rate");
                String level = getString(obj, "level");
                String prestige = getString(obj, "prestige");
                String losses = getString(obj, "losses");
                String wins = getString(obj, "wins");

                obj = type.getJSONObject("game_stats");
                String medals = getString(obj, "medals");
                String goldMedals = getString(obj, "medals_gold");
                String silverMedals = getString(obj, "medals_silver");
                String deaths = getString(obj, "deaths");
                String kills = getString(obj, "solo_kills");
                String killStreak = getString(obj, "kill_streak_best");

                ArrayList<String> info = new ArrayList<String>();
                info.add("Level: " + String.valueOf(Integer.valueOf(level) + Integer.valueOf(prestige) * 100));
                info.add("Tier: " + tier);
                info.add("Total games: " + games);
                info.add("Wins: " + wins);
                info.add("Losses: " + losses);
                info.add("Win rate: " + win_rate + "%");
                info.add("Kills: " + kills);
                info.add("Deaths: " + deaths);
                info.add("Best kill streak: " + killStreak);
                info.add("Medals: " + medals);
                info.add("Golden medals: " + goldMedals);
                info.add("Silver medals: " + silverMedals);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.statistics_itemview, info);

                ListView listView = (ListView) findViewById(R.id.ow_stats);
                listView.setAdapter(adapter);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
