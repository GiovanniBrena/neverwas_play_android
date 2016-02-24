package com.neverwasradio.neverwasplayer.Core;

import android.os.AsyncTask;

import com.neverwasradio.neverwasplayer.UI.Activities.MainActivity;
import com.neverwasradio.neverwasplayer.UI.Activities.MainMenuActivity;

/**
 * Created by Giovanni on 19/02/16.
 */
public class StreamingDataLoader extends AsyncTask<Void,Void,Void> {
    @Override
    protected Void doInBackground(Void... voids) {

        PlayerService.initializePlayer();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(PlayerService.isActive()) {
            MainMenuActivity.updatePlayerIcon(MainMenuActivity.PlayState.PLAY);
        }
    }
}