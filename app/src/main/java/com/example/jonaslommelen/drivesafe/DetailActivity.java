package com.example.jonaslommelen.drivesafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView mDisplayUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDisplayUrl = (TextView) findViewById(R.id.display_url);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
            String extraText = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            mDisplayUrl.setText(extraText);
        }
    }
}
