package com.example.jonaslommelen.drivesafe.Utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        final String BDB_DESCRIPTION = "description";

        final String BDB_PERCENTAGE = "abv";
        //final String BDB_BITTERNESS = "ibu";

        final String BDB_LABEL = "labels";
        final String BDB_LABEL_ICON = "icon";
        //final String BDB_LABEL_MEDIUM = "medium";
        //final String BDB_LABEL_LARGE = "large";

        String[] parsedBeerData;

        JSONObject beerObject = new JSONObject(beerJsonStr);

        /* Is there an error? */
        if (beerObject.has(BDB_ERROR_MESSAGE)) {
            String errorMessage = beerObject.getString(BDB_ERROR_MESSAGE);
            parsedBeerData = new String[1];
            parsedBeerData[0] = errorMessage;
            return parsedBeerData;
        }
        else if(!beerObject.has(BDB_TOTAL_RESULTS)){
            String message = "@string/no_results_message";
            parsedBeerData = new String[1];
            parsedBeerData[0] = message;
            return parsedBeerData;
        }
        else {

            JSONArray beerArray = beerObject.getJSONArray(BDB_DATA);

            parsedBeerData = new String[beerArray.length()*2];


            for (int i = 0; i < beerArray.length(); i++) {
                JSONObject beerJson = beerArray.getJSONObject(i);

                String name = beerJson.getString(BDB_NAME);
                String percentage = beerJson.getString(BDB_PERCENTAGE);
                String id = beerJson.getString(BDB_ID);

                String description = "No description available.";
                if(beerJson.getString(BDB_DESCRIPTION) != null) {
                    description = beerJson.getString(BDB_DESCRIPTION);
                }

                parsedBeerData[2*i] = name + " - " + "percentage: " + percentage;
                parsedBeerData[2*i+1] = id;
            }
            return parsedBeerData;
        }

    }
}
