package com.example.jonaslommelen.drivesafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jonaslommelen.drivesafe.utilities.JsonUtils;
import com.example.jonaslommelen.drivesafe.utilities.URLBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;
    private TextView mDisplayUrl;
    private Button mMaakUrlButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);
        mDisplayUrl = (TextView) findViewById(R.id.url_display);
        mMaakUrlButton = (Button) findViewById(R.id.make_url);

        mMaakUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeBreweryDBSearchQuery();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuOption = item.getItemId();
        if(selectedMenuOption == R.id.action_search){
            makeBreweryDBSearchQuery();
        }
        else if (selectedMenuOption == R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            String string = mSearchBoxEditText.getText().toString();
            intent.putExtra(Intent.EXTRA_TEXT, string);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeBreweryDBSearchQuery() {
        String githubSearchQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchURL = URLBuilder.buildUrl(githubSearchQuery);
        String beerJsonStr = null;
        try {
            beerJsonStr = URLBuilder.getResponseFromHttpUrl(githubSearchURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String response[] = JsonUtils.getBeerStringsFromJson(this, beerJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
