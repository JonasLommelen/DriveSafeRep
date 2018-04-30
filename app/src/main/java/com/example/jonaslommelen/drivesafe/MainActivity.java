package com.example.jonaslommelen.drivesafe;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;

import com.example.jonaslommelen.drivesafe.Utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {// implements BeerAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<String> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mBeersList;
    private Button mAddBeerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddBeerButton = (Button) findViewById(R.id.add_beer_button);

        mAddBeerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startAddBeerActivity = new Intent(MainActivity.this, AddBeerActivity.class);
                startActivity(startAddBeerActivity);
                return;
            }
        });
        /*
        if (savedInstanceState != null) {
            String searchItem = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
            mSearchBoxEditText.setText(searchItem);
            makeBreweryDBSearchQuery();
        }

        getSupportLoaderManager().initLoader(BREWERYDB_SEARCH_LOADER, null, this);
        logAndAppend("onCreate");
        */
    }
    /*
    @Override
    protected void onStart() {
        super.onStart();
        logAndAppend("onStart");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logAndAppend("onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuOption = item.getItemId();
        switch (selectedMenuOption) {
            case R.id.action_search:
                makeBreweryDBSearchQuery();

            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeBreweryDBSearchQuery() {

        String breweryDBSearchQuery = mSearchBoxEditText.getText().toString();
        if (TextUtils.isEmpty(breweryDBSearchQuery)) {
            mSearchBoxEditText.setText(R.string.nothing_entered);
            return;
        }

        URL breweryDBSearchURL = NetworkUtils.buildUrl(breweryDBSearchQuery);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, breweryDBSearchURL.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> breweryDBSearchLoader = loaderManager.getLoader(BREWERYDB_SEARCH_LOADER);
        if (breweryDBSearchLoader == null) {
            loaderManager.initLoader(BREWERYDB_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(BREWERYDB_SEARCH_LOADER, queryBundle, this);
        }
    }

    @Override
    public void onListItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        mCursor.moveToPosition(position);
        String name = mCursor.getString(mCursor.getColumnIndex("name"));
        intent.putExtra("name", name);
        String description = mCursor.getString(mCursor.getColumnIndex("description"));
        intent.putExtra("description", description);
        String abv = mCursor.getString(mCursor.getColumnIndex("abv"));
        intent.putExtra("abv", abv);
        startActivity(intent);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this){

            String mBreweryDBJson;

            @Override
            protected void onStartLoading() {

                if (args == null) {
                    return;
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);

                if (mBreweryDBJson != null) {
                    deliverResult(mBreweryDBJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);

                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }

                try {
                    URL breweryDBUrl = new URL(searchQueryUrlString);
                    String breweryDBSearchResults = NetworkUtils.getResponseFromHttpUrl(breweryDBUrl);
                    return breweryDBSearchResults;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "loadInBackground: IOException", e);
                    return null;
                }
            }

            @Override
            public void deliverResult(String breweryDBJson) {
                mBreweryDBJson = breweryDBJson;
                super.deliverResult(breweryDBJson);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if(data == null){
            showErrorMessage();
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            mBeersList = (RecyclerView) findViewById(R.id.rv_beers);
            mBeersList.setLayoutManager(new LinearLayoutManager(this));
            mBeersList.setHasFixedSize(true);

            mCursor = getJSONCursor(data);
            mAdapter = new BeerAdapter(this, mCursor, this);
            mBeersList.setAdapter(mAdapter);

            showBeerDataView();
        }
    }

    private Cursor getJSONCursor(String response){
        try{
            JSONObject beerObject = new JSONObject(response);
            JSONArray beerArray = beerObject.getJSONArray("data");
            return new JSONArrayCursor(beerArray);
        }
        catch(JSONException exception){
            String ex = exception.getMessage();
        }
        return null;
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        logAndAppend("onSaveInstanceState");
        String searchItem = mSearchBoxEditText.getText().toString();
        outState.putString(SEARCH_QUERY_URL_EXTRA, searchItem);
    }

    private void showBeerDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void logAndAppend(String lifecycleEvent) {
        Log.d(TAG, "Lifecycle Event: " + lifecycleEvent);
    }
    */
}
