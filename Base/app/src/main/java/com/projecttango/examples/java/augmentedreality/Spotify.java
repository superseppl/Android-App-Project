package com.projecttango.examples.java.augmentedreality;

import android.app.Activity;
import android.os.Bundle;

public class Spotify extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // COMMENT
    }

}
