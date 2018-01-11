package com.projecttango.examples.java.augmentedreality;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendStart(View view) {
        Intent intent = new Intent(this, AugmentedRealityActivity.class);
        startActivity(intent);
    }
}
