package com.example.jonaslommelen.drivesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = DetailActivity.class.getSimpleName();
    private String mName;
    private String mAbv;
    private String mDescription;
    private TextView mNameDisplay;
    private TextView mAbvDisplay;
    private TextView mDescriptionDisplay;

    private boolean SHARED_PREFERENCES_HAVE_BEEN_CHANGED = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        mNameDisplay = (TextView) findViewById(R.id.beer_name);
        mAbvDisplay = (TextView) findViewById(R.id.beer_abv);
        mDescriptionDisplay = (TextView) findViewById(R.id.beer_description);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("name")) {
                mName = intentThatStartedThisActivity.getStringExtra("name");
                mNameDisplay.setText(mName);
            }
            if (intentThatStartedThisActivity.hasExtra("abv")) {
                mAbv = prefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default)) + ": " + intentThatStartedThisActivity.getStringExtra("abv");
                mAbvDisplay.setText(mAbv);
            }
            if (intentThatStartedThisActivity.hasExtra("description")) {
                mDescription = intentThatStartedThisActivity.getStringExtra("description");
                mDescriptionDisplay.setText(mDescription);
            }
        }


        if (savedInstanceState != null) {
            String name = savedInstanceState.getString("name");
            String abv = savedInstanceState.getString("abv");
            String description = savedInstanceState.getString("description");
            mNameDisplay.setText(name);
            mAbvDisplay.setText(abv);
            mDescriptionDisplay.setText(description);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SHARED_PREFERENCES_HAVE_BEEN_CHANGED) {
            Log.d(TAG, "onStart: preferences were updated");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            mAbv = prefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default)) + ": " + 100;
            mAbvDisplay.setText(mAbv);
            SHARED_PREFERENCES_HAVE_BEEN_CHANGED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    //TODO detail verdwijnt na aanpassen settings
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //logAndAppend("onSaveInstanceState");
        String name = mNameDisplay.getText().toString();
        String abv = mAbvDisplay.getText().toString();
        String description = mDescriptionDisplay.getText().toString();
        outState.putString("name", name);
        outState.putString("name", abv);
        outState.putString("name", description);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        SHARED_PREFERENCES_HAVE_BEEN_CHANGED = true;
    }
}
