package com.example.jonaslommelen.drivesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jonaslommelen.drivesafe.Data.BeerListContract;
import com.example.jonaslommelen.drivesafe.Data.BeerListDBHelper;
import com.example.jonaslommelen.drivesafe.Data.DriveSafePreferences;

import java.sql.Timestamp;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements MainBeerAdapter.ListItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mBeersList;
    private MainBeerAdapter mAdapter;
    private Button mAddBeerButton;
    private TextView mBloodAlcoholTV;
    private TextView mHoursUntilSafeTV;
    private SQLiteDatabase mDb;
    private Cursor mCursor;

    private int mWeight;
    private int mHeight;
    private boolean mIsMale;
    private double mBloodAlcohol;
    private double mHoursUntilSafe;

    private String NAME_KEY = "name";
    private String ABV_KEY = "abv";
    private String DESCRIPTION_KEY = "description";

    private boolean SHARED_PREFERENCES_HAVE_BEEN_CHANGED = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        mAddBeerButton = (Button) findViewById(R.id.add_beer_button);
        mBloodAlcoholTV = (TextView) findViewById(R.id.blood_alcohol_tv);
        mHoursUntilSafeTV = (TextView) findViewById(R.id.hours_until_safe_tv);

        mAddBeerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startAddBeerActivity = new Intent(MainActivity.this, AddBeerActivity.class);
                startActivity(startAddBeerActivity);
                return;
            }
        });

        BeerListDBHelper dbHelper = new BeerListDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mCursor = getAllBeers();
        mAdapter = new MainBeerAdapter(this, mCursor, this);
        mBeersList = (RecyclerView) findViewById(R.id.rv_beers);
        mBeersList.setLayoutManager(new LinearLayoutManager(this));
        mBeersList.setHasFixedSize(true);
        mBeersList.setAdapter(mAdapter);

        mWeight = Integer.parseInt(DriveSafePreferences.getWeight(this));
        mHeight = Integer.parseInt(DriveSafePreferences.getHeight(this));
        mIsMale = DriveSafePreferences.isMale(this);

        mBloodAlcohol = 0;
        mHoursUntilSafe = 0;
        calculateBloodAlcohol();
        calculateHoursUntilDrivingSafe();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                removeBeer(id);
                updateRecyclerView();
                calculateBloodAlcohol();
                calculateHoursUntilDrivingSafe();
            }

        }).attachToRecyclerView(mBeersList);


        logAndAppend("onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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

    private void updateRecyclerView(){
        Cursor cursor = getAllBeers();
        mAdapter = new MainBeerAdapter(this, cursor, this);
        mBeersList = (RecyclerView) findViewById(R.id.rv_beers);
        mBeersList.setLayoutManager(new LinearLayoutManager(this));
        mBeersList.setHasFixedSize(true);
        mBeersList.setAdapter(mAdapter);
    }

    private void calculateBloodAlcohol(){
        String[] projection = {
                BeerListContract.BeerListEntry.COLUMN_ABV,
                BeerListContract.BeerListEntry.COLUMN_QUANTITY_IN_CL,
                BeerListContract.BeerListEntry.COLUMN_TIMESTAMP
        };

        long tenHoursAgoInMillis = System.currentTimeMillis() - 36000000;
        String selection = BeerListContract.BeerListEntry.COLUMN_TIMESTAMP + " > ?";
        String title = "" + tenHoursAgoInMillis;
        String[] selectionArgs = { title };

        Cursor cursor = mDb.query(
                BeerListContract.BeerListEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        double totalStandardGlasses = 0;
        long hoursSinceFirstDrink = 10;

        if(cursor.getCount() == 0){
            mBloodAlcohol = 0;
            String bloodAlcoholString = "BAC: "+mBloodAlcohol;
            mBloodAlcoholTV.setText(bloodAlcoholString);
            return;
        }

        for (int i = 0; i < cursor.getCount(); i++) {
            double standardGlasses = 0;
            cursor.moveToPosition(i);
            if (i == 0) {
                long timeOfFirstDrink = cursor.getLong(cursor.getColumnIndex(BeerListContract.BeerListEntry.COLUMN_TIMESTAMP));
                long millisSinceFirstDrink = System.currentTimeMillis() - timeOfFirstDrink;
                hoursSinceFirstDrink = millisSinceFirstDrink / 3600000;
                System.out.print(hoursSinceFirstDrink);
            }
            double abv = cursor.getDouble(cursor.getColumnIndex(BeerListContract.BeerListEntry.COLUMN_ABV));
            double stdAbv = abv / 5.2;
            int quantityInCl = cursor.getInt(cursor.getColumnIndex(BeerListContract.BeerListEntry.COLUMN_QUANTITY_IN_CL));
            double stdCl = quantityInCl / 25;

            standardGlasses = stdAbv * stdCl;

            totalStandardGlasses = totalStandardGlasses+standardGlasses;
            Log.d(TAG, "calculateBloodAlcohol: totalStandardGlasses: " + totalStandardGlasses);
        }

        double multiplier;
        if (mIsMale){
            multiplier = 0.7;
        }else{
            multiplier = 0.5;
        }
        double BAC = (totalStandardGlasses*10)/(mWeight*multiplier) - (hoursSinceFirstDrink - 0.5) * (mWeight * 0.002);
        mBloodAlcohol = (double) Math.round(BAC * 100)/100;

        String bloodAlcoholString = "BAC: "+mBloodAlcohol;
        mBloodAlcoholTV.setText(bloodAlcoholString);
    }

    private void calculateHoursUntilDrivingSafe(){
        double NDP = 10d; //n decimal places
        double HUS = mBloodAlcohol/mWeight/0.002/2;
        Log.d(TAG, "calculateHoursUntilDrivingSafe: HUS: " + HUS);
        mHoursUntilSafe = Math.round(HUS*NDP)/NDP;
        String hoursUntilSafeString = "Hours until you can drive again: "+mHoursUntilSafe;
        mHoursUntilSafeTV.setText(hoursUntilSafeString);
    }

    private Cursor getAllBeers() {
        return mDb.query(
                BeerListContract.BeerListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                BeerListContract.BeerListEntry.COLUMN_TIMESTAMP
        );
    }

    private boolean removeBeer(long id) {
        return mDb.delete(BeerListContract.BeerListEntry.TABLE_NAME, BeerListContract.BeerListEntry._ID + "=" + id, null) > 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        logAndAppend("onStart");
        BeerListDBHelper dbHelper = new BeerListDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        updateRecyclerView();
        Log.i(TAG, "onStart: new beerlist added");

        if (SHARED_PREFERENCES_HAVE_BEEN_CHANGED) {
            Log.i(TAG, "onStart: preferences were updated");
            mWeight = Integer.parseInt(DriveSafePreferences.getWeight(this));
            mHeight = Integer.parseInt(DriveSafePreferences.getHeight(this));
            mIsMale = DriveSafePreferences.isMale(this);
            SHARED_PREFERENCES_HAVE_BEEN_CHANGED = false;
        }
        calculateBloodAlcohol();
        calculateHoursUntilDrivingSafe();
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
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        logAndAppend("onDestroy");
    }

    private void logAndAppend(String lifecycleEvent) {
        Log.d(TAG, "Lifecycle Event: " + lifecycleEvent);
    }

    @Override
    public void onListItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        mCursor.moveToPosition(position);
        String name = mCursor.getString(mCursor.getColumnIndex(BeerListContract.BeerListEntry.COLUMN_BEER_NAME));
        String abv = mCursor.getString(mCursor.getColumnIndex(BeerListContract.BeerListEntry.COLUMN_ABV));
        String description = mCursor.getString(mCursor.getColumnIndex(BeerListContract.BeerListEntry.COLUMN_DESCRIPTION));

        intent.putExtra(NAME_KEY, name);
        intent.putExtra(ABV_KEY, abv);
        intent.putExtra(DESCRIPTION_KEY, description);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        SHARED_PREFERENCES_HAVE_BEEN_CHANGED = true;
    }
}
