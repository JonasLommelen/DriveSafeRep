package com.example.jonaslommelen.drivesafe.Data;

import android.provider.BaseColumns;

public class BeerListContract {

    public static final class BeerListEntry implements BaseColumns {
        public static final String TABLE_NAME = "beerList";
        public static final String COLUMN_BEER_NAME = "beerName";
        public static final String COLUMN_QUANTITY_IN_CL = "quantity";
        public static final String COLUMN_ABV = "abv";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

}
