package com.projecttango.examples.java.augmentedreality;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class Voice {

    static String TAG = "Voice";
    static String[] playSynonyms = {"play", "start", "music"};
    static String[] skipSynonyms = {"skip", "next"};
    static String[] pauseSynonyms = {"pause", "stop", "enough"};
    static String[] moreInfoSynonyms = {"Give me more information", "More info please", "More info",
                                    "Information", "Planet information", "Tell me more",
                                    "tell me more please", "tell me more about the planet"};
    static final String[] _urlWikipedia = {"https://en.wikipedia.org/wiki/Mercury_(planet)",
            "https://en.wikipedia.org/wiki/Venus", "https://en.wikipedia.org/wiki/Earth",
            "https://en.wikipedia.org/wiki/Mars", "https://en.wikipedia.org/wiki/Jupiter",
            "https://en.wikipedia.org/wiki/Saturn", "https://en.wikipedia.org/wiki/Uranus",
            "https://en.wikipedia.org/wiki/Neptune"};

    private String _query;
    private String _firstWord;
    private AugmentedRealityActivity _augmentedRealityActivity;

    public Voice(String string, AugmentedRealityActivity _araObject) {
        _query = string;
        _firstWord = getFirstWord(_query);
        _augmentedRealityActivity = _araObject;
    }

    public Voice(String string) {
        _query = string;
        _firstWord = getFirstWord(_query);
    }
    // Look at the query and parse
    public void parseSpotify() {
        if (playCase()) {
            // Spotify play
            Log.i(TAG, "Spotify Play");
        } else if (skipSongCase()) {
            // Spotify skip
            Log.i(TAG, "Spotify Skip");
        } else if (pauseCase()) {
            // Spotify pause
            Log.i(TAG, "Spotify Pause");
        } else if (moreInfoCase()) {
            Log.i(TAG, "More info");
            _augmentedRealityActivity.openWebPage(_urlWikipedia[MainActivity.sphereMap]);
        }

    }

    private boolean playCase() {
        for (int synonym = 0; synonym < playSynonyms.length; ++synonym ) {
            if (_firstWord.equalsIgnoreCase(playSynonyms[synonym]))
                return true;
        }
        return false;
    }

    private boolean pauseCase() {
        for (int synonym = 0; synonym < pauseSynonyms.length; ++synonym ) {
            if (_firstWord.equalsIgnoreCase(pauseSynonyms[synonym]))
                return true;
        }
        return false;
    }

    private boolean skipSongCase() {
        for (int synonym = 0; synonym < skipSynonyms.length; ++synonym ) {
            if (_firstWord.equalsIgnoreCase(skipSynonyms[synonym]))
                return true;
        }
        return false;
    }

    private boolean moreInfoCase() {
        for (int synonym = 0; synonym < moreInfoSynonyms.length; ++synonym ) {
            if (_query.equalsIgnoreCase(moreInfoSynonyms[synonym]))
                return true;
        }
        return false;
    }

    private String getFirstWord(String text) {
        if (text.indexOf(' ') > -1) { // Check if there is more than one word.
            return text.substring(0, text.indexOf(' ')); // Extract first word.
        } else {
            return text; // Text is the first word itself.
        }
    }

}
