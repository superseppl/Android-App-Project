package com.projecttango.examples.java.augmentedreality;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class Voice {

    static String TAG = "Voice";
    static String[] playSynonyms = {"play", "start", "music"};
    static String[] skipSynonyms = {"skip", "next", "skip to next"};
    static String[] replaySynonyms = {"replay", "back", "go back", "play again", "skip to previous"};
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
            // Ifpause the sphere is clicked first time after login the playlist will be played after that each click will be resume/
            if(AugmentedRealityActivity.FirstTimeClicked) {
                AugmentedRealityActivity.mPlayer.playUri(AugmentedRealityActivity.mOperationCallback, "spotify:user:spotify:playlist:37i9dQZF1DWWxPM4nWdhyI", 0, 0);
                AugmentedRealityActivity.FirstTimeClicked = false;
            }
            else if (AugmentedRealityActivity.mCurrentPlaybackState != null){
                AugmentedRealityActivity.mPlayer.resume(AugmentedRealityActivity.mOperationCallback);
            }
            Log.i(TAG, "Spotify Play");
        } else if (skipSongCase()) {
            // Spotify skip
            if (AugmentedRealityActivity.mCurrentPlaybackState != null) {
                AugmentedRealityActivity.mPlayer.skipToNext(AugmentedRealityActivity.mOperationCallback);
            }
            Log.i(TAG, "Spotify Skip");
        } else if(replayCase()) {
            if (AugmentedRealityActivity.mCurrentPlaybackState != null) {
                AugmentedRealityActivity.mPlayer.skipToPrevious(AugmentedRealityActivity.mOperationCallback);
            }
            Log.i(TAG, "Spotify Skip to Previous");
        } else if (pauseCase()) {
            // Spotify pause
            if (AugmentedRealityActivity.mCurrentPlaybackState != null && AugmentedRealityActivity.mCurrentPlaybackState.isPlaying) {
                AugmentedRealityActivity.mPlayer.pause(AugmentedRealityActivity.mOperationCallback);
            }
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
            if (_query.equalsIgnoreCase(skipSynonyms[synonym]))
                return true;
        }
        return false;
    }

    private boolean replayCase() {
        for (int synonym = 0; synonym < replaySynonyms.length; ++synonym ) {
            if (_query.equalsIgnoreCase(replaySynonyms[synonym]))
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
