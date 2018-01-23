package com.projecttango.examples.java.augmentedreality;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    Integer sphereSize = 1;

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
    }

    @Override
    public void onResume() {
        super.onResume();

        sphereSize = getIntent().getIntExtra("size", 1);

        Toast.makeText(MainActivity.this,"seek bar progress:"+ sphereSize,
                Toast.LENGTH_SHORT).show();
    }

    public void sendStart(View view) {
        Intent intent = new Intent(MainActivity.this, AugmentedRealityActivity.class);
        startActivity(intent);
    }

    public void sendSettings(View view) {
        Intent sendSizetoSetting = new Intent(MainActivity.this, Setting.class);
        sendSizetoSetting.putExtra("size", sphereSize);
        startActivity(sendSizetoSetting);
    }

    public void sendSpotify(View view) {
        Intent intent = new Intent(MainActivity.this, Spotify.class);
        startActivity(intent);
    }
}
