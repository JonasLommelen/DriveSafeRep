package com.example.jonaslommelen.drivesafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private String mName;
    private String mAbv;
    private String mDescription;
    private TextView mNameDisplay;
    private TextView mAbvDisplay;
    private TextView mDescriptionDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mNameDisplay = (TextView) findViewById(R.id.beer_name);
        mAbvDisplay = (TextView) findViewById(R.id.beer_abv);
        mDescriptionDisplay = (TextView) findViewById(R.id.beer_description);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("name")) {
                mName = intentThatStartedThisActivity.getStringExtra("name");
                mNameDisplay.setText(mName);
            }
            if (intentThatStartedThisActivity.hasExtra("abv")) {
                mAbv = "abv: " + intentThatStartedThisActivity.getStringExtra("abv");
                mAbvDisplay.setText(mAbv);
            }
            if (intentThatStartedThisActivity.hasExtra("description")) {
                mDescription = intentThatStartedThisActivity.getStringExtra("description");
                mDescriptionDisplay.setText(mDescription);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
