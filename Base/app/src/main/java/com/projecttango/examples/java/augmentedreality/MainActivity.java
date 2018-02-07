package com.projecttango.examples.java.augmentedreality;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.speech.RecognizerIntent;
import android.util.Log;

import android.util.TypedValue;
import android.view.MotionEvent;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static android.speech.RecognizerIntent.EXTRA_RESULTS;

public class MainActivity extends Activity {

    /** Global Variables **/
    static Integer sphereSize = 45;
    static Integer sphereMap = 1;

    static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBack();

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

        //Reciving information
        sphereSize = getIntent().getIntExtra("size", 45);
        sphereMap = getIntent().getIntExtra("map", 1);

        playBack();
    }

    /**Method that will start the AugmentedRealityActivity **/
    public void sendStart(View view) {
        Intent intent = new Intent(MainActivity.this, AugmentedRealityActivity.class);
        startActivity(intent);
    }

    /** Method that will start the SettingsActivity **/
    public void sendSettings(View view) {
        Intent sendSettingsToSetting = new Intent(MainActivity.this, Setting.class);
        sendSettingsToSetting.putExtra("size", sphereSize);
        sendSettingsToSetting.putExtra("map", sphereMap);
        startActivity(sendSettingsToSetting);
    }

    /** Method that will start the SpotifyActivity **/
    public void sendSpotify(View view) {
        Intent intent = new Intent(MainActivity.this, MySpotify.class);
        startActivity(intent);
    }


    /** Method that will start the SpeechActivity **/
    public void sendSpeech(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.projecttango.examples.java.augmentedreality");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1000);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak, Human");
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }


    /** Get the result of the code **/
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            Log.i("On activity result", "Spoken text: " + spokenText);
            Voice _voice = new Voice(spokenText);
            _voice.parseSpotify();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /** Animation for the Background **/
    public void playBack (){
        VideoView videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.solarsystem);
        videoview.setVideoURI(uri);
        videoview.start();

    }
}
