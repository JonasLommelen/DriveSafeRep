package com.example.jonaslommelen.drivesafe.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Jonas Lommelen on 6/03/2018.
 */

public class URLBuilder {

    // base URL of version 2 of the BreweryDB API
    private final static String BREWERYDB_BASE_URL = "http://api.brewerydb.com/v2/";

    private final static String QUERY_PARAM = "q";
    private final static String KEY_PARAM = "key";

    // personal key acquired from BreweryDB, only permitted for this app
    private final static String key = "32f479602aef5621e2fe9f078a2b8abd";

    public static URL buildUrl(String beerQuery){
        Uri builtUri = Uri.parse(BREWERYDB_BASE_URL).buildUpon()
                .appendPath("search")
                .appendQueryParameter(QUERY_PARAM, beerQuery)
                .appendQueryParameter(KEY_PARAM, key)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
