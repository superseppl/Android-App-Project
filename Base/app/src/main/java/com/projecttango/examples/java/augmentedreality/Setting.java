package com.projecttango.examples.java.augmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

public class Setting extends Activity {

    private SeekBar seekBar = null;
    int sphereSize = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        sphereSize = getIntent().getIntExtra("size", 1);
        seekBar = (SeekBar) findViewById(R.id.seekBar2);

        Toast.makeText(Setting.this, "seek bar progress:" + sphereSize,
                Toast.LENGTH_SHORT).show();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sphereSize = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Setting.this, "seek bar progress:" + sphereSize,
                        Toast.LENGTH_SHORT).show();

                //
            }
        });
    }

    public void sendSettingsToMain(View view) {
        Intent sendSizeToMain = new Intent(Setting.this , MainActivity.class);
        sendSizeToMain.putExtra("size", sphereSize);
        startActivity(sendSizeToMain);
    }





}
