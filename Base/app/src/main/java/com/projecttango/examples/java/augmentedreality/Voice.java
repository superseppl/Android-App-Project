package com.projecttango.examples.java.augmentedreality;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Voice class for parsing the Text Input from the Google Speech API
 * and acting accordingly
 */
public class Voice {

    /**
     * Logger TAG
     */
    static String TAG = "Voice";
    /**
     * Synonyms for "play"
     */
    static String[] playSynonyms = {"play", "start", "music"};
    /**
     * Synonyms for "skip"
     */
    static String[] skipSynonyms = {"skip", "next", "skip to next"};
    /**
     * Synonyms for "replay"
     */
    static String[] replaySynonyms = {"replay", "back", "go back", "play again", "skip to previous"};
    /**
     * Synonyms for "pause"
     */
    static String[] pauseSynonyms = {"pause", "stop", "enough"};
    /**
     * Synonyms for "More info"
     */
    static String[] moreInfoSynonyms = {"Give me more information", "More info please", "More info",
                                    "Information", "Planet information", "Tell me more",
                                    "tell me more please", "tell me more about the planet"};
    /**
     * Urls of the Planet Wikipedia Websites
     */
    static final String[] _urlWikipedia = {"https://en.wikipedia.org/wiki/Mercury_(planet)",
            "https://en.wikipedia.org/wiki/Venus", "https://en.wikipedia.org/wiki/Earth",
            "https://en.wikipedia.org/wiki/Mars", "https://en.wikipedia.org/wiki/Jupiter",
            "https://en.wikipedia.org/wiki/Saturn", "https://en.wikipedia.org/wiki/Uranus",
            "https://en.wikipedia.org/wiki/Neptune"};

    /**
     * The query returned by the Google Speech API
     */
    private String _query;
    /**
     * First word of the query -> makes parsing easier
     */
    private String _firstWord;
    /**
     * AugmentedRealityActivity object -> calling methods
     */
    private AugmentedRealityActivity _augmentedRealityActivity;

    /**
     * Constructor 1
     * @param string the Voice Input as a string
     * @param _araObject the AugmentedRealityObject
     */
    public Voice(String string, AugmentedRealityActivity _araObject) {
        _query = string;
        _firstWord = getFirstWord(_query);
        _augmentedRealityActivity = _araObject;
    }

    /**
     * Constructor 2
     * @param string the Voice Input as a string
     */
    public Voice(String string) {
        _query = string;
        _firstWord = getFirstWord(_query);
    }

    /**
     * Parses the Voice Input and starts the correct action
     * - Play/Pause/Replay/Skip music
     * - Open a browser with more info about the planet
     * Should be called after the Voice Object is constructed
     */
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
            // Spotify Replay
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
            // More info about the planet
            Log.i(TAG, "More info");
            _augmentedRealityActivity.openWebPage(_urlWikipedia[MainActivity.sphereMap]);
        }

    }

    /**
     * Is this a "play" voice request?
     * @return return 1 if yes
     */
    private boolean playCase() {
        for (int synonym = 0; synonym < playSynonyms.length; ++synonym ) {
            if (_firstWord.equalsIgnoreCase(playSynonyms[synonym]))
                return true;
        }
        return false;
    }
    /**
     * Is this a "pause" voice request?
     * @return return 1 if yes
     */
    private boolean pauseCase() {
        for (int synonym = 0; synonym < pauseSynonyms.length; ++synonym ) {
            if (_firstWord.equalsIgnoreCase(pauseSynonyms[synonym]))
                return true;
        }
        return false;
    }
    /**
     * Is this a "skip Song" voice request?
     * @return return 1 if yes
     */
    private boolean skipSongCase() {
        for (int synonym = 0; synonym < skipSynonyms.length; ++synonym ) {
            if (_query.equalsIgnoreCase(skipSynonyms[synonym]))
                return true;
        }
        return false;
    }
    /**
     * Is this a "replay" voice request?
     * @return return 1 if yes
     */
    private boolean replayCase() {
        for (int synonym = 0; synonym < replaySynonyms.length; ++synonym ) {
            if (_query.equalsIgnoreCase(replaySynonyms[synonym]))
                return true;
        }
        return false;
    }
    /**
     * Is this a "more Info" voice request?
     * @return return 1 if yes
     */
    private boolean moreInfoCase() {
        for (int synonym = 0; synonym < moreInfoSynonyms.length; ++synonym ) {
            if (_query.equalsIgnoreCase(moreInfoSynonyms[synonym]))
                return true;
        }
        return false;
    }

    /**
     * Get the first word of the query
     * Helps with parsing -> just parsing the first word since they have distinct meanings
     * e.g. play, pause, stop...
     * The "moreInfo" case is parsed as a full query
     * @param text the query
     * @return the first word of the query
     */
    private String getFirstWord(String text) {
        if (text.indexOf(' ') > -1) { // Check if there is more than one word.
            return text.substring(0, text.indexOf(' ')); // Extract first word.
        } else {
            return text; // Text is the first word itself.
        }
    }

}
