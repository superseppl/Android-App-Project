package com.projecttango.examples.java.augmentedreality;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

public class Setting extends Activity {

    private SeekBar seekBar = null;

    int sphereSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        seekBar = (SeekBar)findViewById(R.id.seekBar2);
        sphereSize = 0;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                sphereSize = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Setting.this,"seek bar progress:"+ sphereSize,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }





}
