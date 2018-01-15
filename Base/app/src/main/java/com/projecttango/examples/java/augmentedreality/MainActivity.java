package com.projecttango.examples.java.augmentedreality;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        //saklaösd
    }

    public void sendStart(View view) {
        Intent intent = new Intent(this, AugmentedRealityActivity.class);
        startActivity(intent);
    }

    public void sendSettings(View view) {
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }

    public void sendSpotify(View view) {
        Intent intent = new Intent(this, Spotify.class);
        startActivity(intent);
    }
}
