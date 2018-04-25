package com.example.jonaslommelen.drivesafe;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.jonaslommelen.drivesafe.utilities.JsonUtils;
import com.example.jonaslommelen.drivesafe.utilities.URLBuilder;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements BeerAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<String> {

    private EditText mSearchBoxEditText;
    private TextView mDisplayUrl;
    private TextView mErrorMessageDisplay;
    private TextView mSearchResultsTextView;
    private Button mMaakUrlButton;
    private static final int NUM_LIST_ITEMS = 100;
    private BeerAdapter mAdapter;
    private RecyclerView mBeersList;
    private Toast mToast;
    private ProgressBar mLoadingIndicator;
    private static final int BREWERYDB_SEARCH_LOADER = 22;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);
        mDisplayUrl = (TextView) findViewById(R.id.tv_url_display);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_search_result_display);
        mMaakUrlButton = (Button) findViewById(R.id.make_url);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mMaakUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeBreweryDBSearchQuery();
            }
        });

        mBeersList = (RecyclerView) findViewById(R.id.rv_beers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBeersList.setLayoutManager(layoutManager);
        mBeersList.setHasFixedSize(true);
        mAdapter = new BeerAdapter(NUM_LIST_ITEMS, this);
        mBeersList.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            String queryUrl = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
            mDisplayUrl.setText(queryUrl);
        }

        getSupportLoaderManager().initLoader(BREWERYDB_SEARCH_LOADER, null, this);
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

        String breweryDBSearchQuery = mSearchBoxEditText.getText().toString();
        if (TextUtils.isEmpty(breweryDBSearchQuery)) {
            mDisplayUrl.setText("No query entered, nothing to search for.");
            return;
        }
        URL breweryDBSearchURL = URLBuilder.buildUrl(breweryDBSearchQuery);
        mDisplayUrl.setText(breweryDBSearchURL.toString());

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, breweryDBSearchURL.toString());


        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> githubSearchLoader = loaderManager.getLoader(BREWERYDB_SEARCH_LOADER);
        if (githubSearchLoader == null) {
            loaderManager.initLoader(BREWERYDB_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(BREWERYDB_SEARCH_LOADER, queryBundle, this);
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (mToast != null) {
            mToast.cancel();
        }
        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();
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
                    String breweryDBSearchResults = URLBuilder.getResponseFromHttpUrl(breweryDBUrl);
                    return breweryDBSearchResults;
                } catch (IOException e) {
                    e.printStackTrace();
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
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (data == null) {
            showErrorMessage();
        } else {
            try {
                String[] parsedBeerData = JsonUtils.getBeerStringsFromJson(this, data);
                String name = parsedBeerData[0];
                mSearchResultsTextView.setText(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //mSearchResultsTextView.setText(data);
            showBeerDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String queryUrl = mDisplayUrl.getText().toString();
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl);
    }

    private void showBeerDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mDisplayUrl.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mDisplayUrl.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
