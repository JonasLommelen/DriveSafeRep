package com.example.jonaslommelen.drivesafe.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by Jonas Lommelen on 21/03/2018.
 */

public class JsonUtils {
    public static String[] getBeerStringsFromJson(Context context, String beerJsonStr)
            throws JSONException {

        final String BDB_ERROR_MESSAGE = "errorMessage";
        final String BDB_TOTAL_RESULTS = "totalResults"; // is not present when there are no results
        final String BDB_DATA = "data";

        final String BDB_ID = "id";
        final String BDB_NAME = "name";
        final String BDB_NAMEDISPLAY = "nameDisplay";

        final String BDB_PERCENTAGE = "abv";
        //final String BDB_BITTERNESS = "ibu";

        final String BDB_LABEL = "labels";
        final String BDB_LABEL_ICON = "icon";
        //final String BDB_LABEL_MEDIUM = "medium";
        //final String BDB_LABEL_LARGE = "large";

        String[] parsedBeerData = null;

        JSONObject beerObject = new JSONObject(beerJsonStr);

        /* Is there an error? */
        if (beerObject.has(BDB_ERROR_MESSAGE)) {
            String errorMessage = beerObject.getString(BDB_ERROR_MESSAGE);
            parsedBeerData = new String[1];
            parsedBeerData[0] = errorMessage;
            return parsedBeerData;
        }
        else if(!beerObject.has(BDB_TOTAL_RESULTS)){
            return null;
        }
        else {

            JSONArray beerArray = beerObject.getJSONArray(BDB_DATA);

            parsedBeerData = new String[beerArray.length()];

            for (int i = 0; i < beerArray.length(); i++) {

                JSONObject beerJson = beerArray.getJSONObject(i);
                //JSONObject labelJson = beerJson.getJSONObject(BDB_LABEL);

                String name = beerJson.getString(BDB_NAME);
                //String percentage = beerJson.getString(BDB_PERCENTAGE);
                //String labelPng = null;
                //if (labelJson != null) {
                //    labelPng = labelJson.getString(BDB_LABEL_ICON);
                //}

                parsedBeerData[i] = name; // + "-" + percentage;// + "-" + labelPng;
            }
            return parsedBeerData;
        }

    }
}
