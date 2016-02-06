package com.neverwasradio.neverwasplayer.Core;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.neverwasradio.neverwasplayer.UI.Activities.PlayerFragment;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlayerService extends IntentService {

    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";

    private static boolean active;
    private static MediaPlayer player;
    private static PlayerFragment playerFragment;
    private static MediaPlayer.TrackInfo trackInfo[];
    public static final String RADIO_URL_SOURCE = "http://www.associazionesmart.it/cgi-bin/picoreader.cgi?user=neverwas&r=1362900603&f=file.mp3";

    public static boolean isActive() {return active;}


    public static void startActionStop(Context context) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }


    public static void startActionStart(Context context) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public PlayerService() {
        super("PlayerService");
    }

    public static void setPlayerFragment(PlayerFragment fragment){ playerFragment=fragment;}
    public static PlayerFragment getPlayerFragment() {return playerFragment;}

    // CHOOSE ACTION TODO
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_STOP.equals(action)) {
                handleActionStop();

            } else if (ACTION_START.equals(action)) {
                handleActionStart();
            }
        }
    }

    // STOP ACTIONS
    private void handleActionStop() {
        active=false;
        player.stop();
    }

    // START ACTIONS
    private void handleActionStart() {
        active=true;
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initializePlayer() {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(RADIO_URL_SOURCE);
        } catch (IOException e) {
            Log.e("STREAM AUDIO", "url error");
            e.printStackTrace();
            playerFragment.showErrorConnection();
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();
                //trackInfo = player.getTrackInfo();
                playerFragment.setPauseButton();
                playerFragment.stopTimeoutTask();
                Log.e("STREAM AUDIO", "player ready");
            }
        });

    }


}

