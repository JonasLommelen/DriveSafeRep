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

    private String NAME_KEY = "name";
    private String ABV_KEY = "abv";
    private String DESCRIPTION_KEY = "description";

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
            if (intentThatStartedThisActivity.hasExtra(NAME_KEY)) {
                mName = intentThatStartedThisActivity.getStringExtra(NAME_KEY);
                mNameDisplay.setText(mName);
            }
            if (intentThatStartedThisActivity.hasExtra(ABV_KEY)) {
                mAbv = intentThatStartedThisActivity.getStringExtra("abv");
                mAbvDisplay.setText(prefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default)) + ": " + mAbv);
            }
            if (intentThatStartedThisActivity.hasExtra(DESCRIPTION_KEY)) {
                mDescription = intentThatStartedThisActivity.getStringExtra(DESCRIPTION_KEY);
                mDescriptionDisplay.setText(mDescription);
            }
        }

        if (savedInstanceState != null) {
            mName = savedInstanceState.getString(NAME_KEY);
            mAbv = savedInstanceState.getString(ABV_KEY);
            mDescription = savedInstanceState.getString(DESCRIPTION_KEY);
            mNameDisplay.setText(mName);
            mAbvDisplay.setText(mAbv);
            mDescriptionDisplay.setText(mDescription);
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
            mAbvDisplay.setText(prefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default)) + ": " + mAbv);
            SHARED_PREFERENCES_HAVE_BEEN_CHANGED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logAndAppend("onDestroy");
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        logAndAppend("onSaveInstanceState");
        outState.putString(NAME_KEY, mName);
        outState.putString(ABV_KEY, mAbv);
        outState.putString(DESCRIPTION_KEY, mDescription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logAndAppend("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logAndAppend("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        logAndAppend("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logAndAppend("onRestart");
    }

    private void logAndAppend(String lifecycleEvent) {
        Log.d(TAG, "Lifecycle Event: " + lifecycleEvent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            Log.i(TAG, "action settings was selected");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        SHARED_PREFERENCES_HAVE_BEEN_CHANGED = true;
    }
}
