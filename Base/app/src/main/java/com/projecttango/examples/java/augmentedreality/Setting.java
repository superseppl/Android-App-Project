package com.projecttango.examples.java.augmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class Setting extends Activity implements AdapterView.OnItemSelectedListener {

    int sphereSize;
    int sphereMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        sphereSize = getIntent().getIntExtra("size", 45);
        sphereMap = getIntent().getIntExtra("map", 1);

        //Set the Spinner with Layout, ArrayAdapter, Value
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planetsArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(sphereMap);
        spinner.setOnItemSelectedListener(this);

        ////Set the Seekbar with Progress and Listener
        SeekBar seekBar= (SeekBar) findViewById(R.id.seekBar2);
        seekBar.setProgress(sphereSize);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sphereSize = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Setting.this, "Size of Sphere is " + sphereSize,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        //Toast.makeText(Setting.this, "Spinner Data is " + pos,
        //       Toast.LENGTH_SHORT).show();

        sphereMap = pos;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void sendSettingsToMain(View view) {
        Intent sendSettingsToMain = new Intent(Setting.this , MainActivity.class);
        sendSettingsToMain.putExtra("size", sphereSize);
        sendSettingsToMain.putExtra("map", sphereMap);
        startActivity(sendSettingsToMain);
    }

    @Override
    public void onBackPressed() {
        Intent sendSettingsToMain = new Intent(Setting.this , MainActivity.class);
        sendSettingsToMain.putExtra("size", sphereSize);
        sendSettingsToMain.putExtra("map", sphereMap);
        startActivity(sendSettingsToMain);
        super.onBackPressed();
    }
}
