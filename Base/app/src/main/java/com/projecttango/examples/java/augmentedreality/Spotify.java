package com.projecttango.examples.java.augmentedreality;

import android.app.Activity;
import android.app.SearchManager;
import android.os.Bundle;
import android.util.Log;

public class Spotify extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
