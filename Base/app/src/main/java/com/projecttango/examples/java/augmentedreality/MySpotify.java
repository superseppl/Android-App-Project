package com.projecttango.examples.java.augmentedreality;

import android.app.Activity;
import android.os.Bundle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.picasso.Picasso;

public class MySpotify extends Activity implements
        Player.NotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "4b87f575fa9b4019ab1f575aaf71228b";
    private static final String REDIRECT_URI = "my-spotify-app-login://callback";
    private static final int REQUEST_CODE = 1337;
    private static final String ALBUM_URI = "spotify:user:spotify:playlist:37i9dQZF1DWWxPM4nWdhyI";
    public static final String TAG = "MyAdvancedSpotifyApp";

    /**
     * UI controls which may only be enabled after the player has been initialized,
     * (or effectively, after the user has logged in).
     */
    private static final int[] REQUIRES_INITIALIZED_STATE = {
            R.id.play_button,
    };

    /**
     * UI controls which should only be enabled if the player is actively playing.
     */
    private static final int[] REQUIRES_PLAYING_STATE = {
            R.id.next_button,
            R.id.prev_button,
    };

    private SpotifyPlayer mPlayer;
    private PlaybackState mCurrentPlaybackState;
    private BroadcastReceiver mNetworkStateReceiver;
    private Metadata mMetadata;
    private TextView mMetadataText;
    private boolean FirstTimeClicked = true;

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "OK");
        }

        @Override
        public void onError(Error error) {
            Log.d(TAG, "ERROR");
        }
    };

    // INITIALIZATION

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mMetadataText = (TextView) findViewById(R.id.metadata);

        updateView();
        
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up the broadcast receiver for network events. Note that we also unregister
        // this receiver again in onPause().
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPlayer != null) {
                    Connectivity connectivity = getNetworkConnectivity(getBaseContext());
                    mPlayer.setConnectivityStatus(mOperationCallback, connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);

        if (mPlayer != null) {
            mPlayer.addNotificationCallback(MySpotify.this);
            mPlayer.addConnectionStateCallback(MySpotify.this);
        }
    }

    private Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }
    }

    // AUTHENTICATION

    private void openLoginWindow() {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setShowDialog(true)
                .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming"})
                .build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    onAuthenticationComplete(response);
                    break;
                case ERROR:
                    Log.d(TAG, "auth error...");
                    break;
                default:
                    Log.d(TAG, "auth result..." + response.getType());
            }
        }
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {

        Log.d(TAG, "got authentication token");
        if (mPlayer == null) {

            Config playerConfig = new Config(getApplicationContext(), authResponse.getAccessToken(), CLIENT_ID);
            mPlayer = com.spotify.sdk.android.player.Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {
                    Log.d(TAG, "\"-- Player initialized --\"");
                    mPlayer.setConnectivityStatus(mOperationCallback, getNetworkConnectivity(MySpotify.this));
                    mPlayer.addNotificationCallback(MySpotify.this);
                    mPlayer.addConnectionStateCallback(MySpotify.this);
                    updateView();
                }

                @Override
                public void onError(Throwable error) {
                    Log.d(TAG, "\"Error in initialization: \"" + error.getMessage());
                }
            });
        } else {
            mPlayer.login(authResponse.getAccessToken());
        }
    }

    // UI EVENTS

    private void updateView() {
        boolean loggedIn = isLoggedIn();

        // Login button should be the inverse of the logged in state
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setText(loggedIn ? "LOGOUT" : "LOGIN");

        // Set enabled for all widgets which depend on initialized state
        for (int id : REQUIRES_INITIALIZED_STATE) {
            findViewById(id).setEnabled(loggedIn);
        }

        // Same goes for the playing state
        boolean playing = loggedIn && mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying;
        for (int id : REQUIRES_PLAYING_STATE) {
            findViewById(id).setEnabled(playing);
        }

        if (mMetadata != null) {
            findViewById(R.id.next_button).setEnabled(mMetadata.nextTrack != null);
            findViewById(R.id.prev_button).setEnabled(mMetadata.prevTrack != null);
        }

        final ImageView coverArtView = (ImageView) findViewById(R.id.cover_art);

        if (mMetadata != null && mMetadata.currentTrack != null) {
            final String durationStr = String.format(" (%dms)", mMetadata.currentTrack.durationMs);
            mMetadataText.setText(mMetadata.contextName + "\n" + mMetadata.currentTrack.name + " - " + mMetadata.currentTrack.artistName + durationStr);

            Picasso.with(this)
                    .load(mMetadata.currentTrack.albumCoverWebUrl)
                    .into(coverArtView);
        } else {
            mMetadataText.setText("nothing is playing");
            coverArtView.setBackground(null);
        }

    }

    private boolean isLoggedIn() {
        return mPlayer != null && mPlayer.isLoggedIn();
    }

    public void onLoginButtonClicked(View view) {
        if (!isLoggedIn()) {
            Log.d("msg", "Logging in");
            openLoginWindow();
        } else {
            Log.d("msg", "Logging out");
            mPlayer.logout();
            FirstTimeClicked = true;
        }
    }

    public void onPlayButtonClicked(View view) {

        // you can play multiple songs, playlists if you want
        String uri = ALBUM_URI;

        Log.d("msg", "Starting playback for " + uri);

        if(FirstTimeClicked) {
            mPlayer.playUri(mOperationCallback, uri, 0, 0);
            FirstTimeClicked = false;
        }
        else if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
            mPlayer.pause(mOperationCallback);
        }
        else {
            mPlayer.resume(mOperationCallback);
        }
    }

    public void onPreviousButtonClicked(View view) {
        mPlayer.skipToPrevious(mOperationCallback);
    }

    public void onNextButtonClicked(View view) {
        mPlayer.skipToNext(mOperationCallback);
    }

    // CALLBACK METHODS

    @Override
    public void onLoggedIn() {
        Log.d("msg", "Login complete");
        updateView();
    }

    @Override
    public void onLoggedOut() {
        Log.d("msg", "Logout complete");
        updateView();
    }

    public void onLoginFailed(Error error) {
        Log.d("msg", "Login error" + error);
    }

    @Override
    public void onTemporaryError() {
        Log.d("msg", "Temporary error occured");
    }

    @Override
    public void onConnectionMessage(final String message) {
        Log.d(TAG, "Incoming connection message: " + message);
    }

    // DESTRUCTION

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNetworkStateReceiver);

        if (mPlayer != null) {
            mPlayer.removeNotificationCallback(MySpotify.this);
            mPlayer.removeConnectionStateCallback(MySpotify.this);
        }
    }


    @Override
    protected void onDestroy() {
        com.spotify.sdk.android.player.Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent event) {
        Log.d(TAG, "Event: " + event);
        mCurrentPlaybackState = mPlayer.getPlaybackState();
        mMetadata = mPlayer.getMetadata();
        Log.i(TAG, "Player state: " + mCurrentPlaybackState);
        Log.i(TAG, "Metadata: " + mMetadata);
        updateView();
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d(TAG, "Err: " + error);
    }

}
