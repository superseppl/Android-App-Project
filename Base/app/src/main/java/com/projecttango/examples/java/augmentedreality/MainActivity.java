package com.projecttango.examples.java.augmentedreality;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    static Integer sphereSize = 45;
    static Integer sphereMap = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the status bar.
        //View decorView = getWindow().getDecorView();
        //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();
    }

    @Override
    public void onResume() {
        super.onResume();

        sphereSize = getIntent().getIntExtra("size", 45);
        sphereMap = getIntent().getIntExtra("map", 1);

    }

    public void sendStart(View view) {
        Intent intent = new Intent(MainActivity.this, AugmentedRealityActivity.class);
        startActivity(intent);
    }

    public void sendSettings(View view) {
        Intent sendSettingsToSetting = new Intent(MainActivity.this, Setting.class);
        sendSettingsToSetting.putExtra("size", sphereSize);
        sendSettingsToSetting.putExtra("map", sphereMap);
        startActivity(sendSettingsToSetting);
    }

    public void sendSpotify(View view) {
        Intent intent = new Intent(MainActivity.this, Spotify.class);
        startActivity(intent);
    }
}
