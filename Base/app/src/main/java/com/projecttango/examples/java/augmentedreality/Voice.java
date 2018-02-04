package com.projecttango.examples.java.augmentedreality;

import android.util.Log;

public class Voice {

    static String TAG = "Voice";
    static String[] playSynonyms = {"play", "start", "music"};
    static String[] skipSynonyms = {"skip", "next"};
    static String[] pauseSynonyms = {"pause", "stop", "enough"};


    public Voice(String string) {
        _query = string;
        _firstWord = getFirstWord(_query);
    }

    private String _query;
    private String _firstWord;

    // Look at the query and parse
    public void parseSpotify() {
        if (playCase(_firstWord)) {
            // Spotify play
            Log.i(TAG, "Spotify Play");
        } else if (skipSongCase(_firstWord)) {
            // Spotify skip
            Log.i(TAG, "Spotify Skip");
        } else if (pauseCase(_firstWord)) {
            // Spotify pause
            Log.i(TAG, "Spotify Pause");
        }

    }
    private boolean playCase(String _query) {
        for (int synonym = 0; synonym < playSynonyms.length; ++synonym ) {
            if (_query.equalsIgnoreCase(playSynonyms[synonym]))
                return true;
        }
        return false;
    }
    private boolean pauseCase(String _query) {
        for (int synonym = 0; synonym < pauseSynonyms.length; ++synonym ) {
            if (_query.equalsIgnoreCase(pauseSynonyms[synonym]))
                return true;
        }
        return false;
    }
    private boolean skipSongCase(String _query) {
        for (int synonym = 0; synonym < skipSynonyms.length; ++synonym ) {
            if (_query.equalsIgnoreCase(skipSynonyms[synonym]))
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
