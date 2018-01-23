package com.projecttango.examples.java.augmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

public class Setting extends Activity {

    private SeekBar seekBar = null;
    int sphereSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        sphereSize = getIntent().getIntExtra("size", 1);
        seekBar = (SeekBar) findViewById(R.id.seekBar2);
        seekBar.setProgress(sphereSize);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sphereSize = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Setting.this, "Size of Sphere is" + sphereSize,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendSettingsToMain(View view) {
        Intent sendSizeToMain = new Intent(Setting.this , MainActivity.class);
        sendSizeToMain.putExtra("size", sphereSize);
        startActivity(sendSizeToMain);
    }

    @Override
    public void onBackPressed() {
        Intent sendSizeToMain = new Intent(Setting.this , MainActivity.class);
        sendSizeToMain.putExtra("size", sphereSize);
        startActivity(sendSizeToMain);

        super.onBackPressed();
    }
}
