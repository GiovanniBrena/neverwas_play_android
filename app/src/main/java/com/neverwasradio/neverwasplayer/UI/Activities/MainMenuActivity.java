package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.FacebookSdk;
import com.google.analytics.tracking.android.EasyTracker;
import com.neverwasradio.neverwasplayer.Core.PlayerService;
import com.neverwasradio.neverwasplayer.Core.StreamingDataLoader;
import com.neverwasradio.neverwasplayer.Model.NWProgramManager;
import com.neverwasradio.neverwasplayer.R;
import com.neverwasradio.neverwasplayer.UI.CustomView.MenuSection;

import java.util.Timer;
import java.util.TimerTask;

public class MainMenuActivity extends Activity {

    static MenuSection sectionPlayer;
    MenuSection sectionTimeTable;
    MenuSection sectionNews;
    MenuSection sectionChat;
    MenuSection sectionSocial;

    PlayerServiceLauncher serviceLauncher;

    Timer preLoadingTimer;

    boolean playerLock;


    public enum PlayState {
        PLAY,
        STOP,
        BUFFERING,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sectionPlayer = (MenuSection) findViewById(R.id.menuSectionPlayer);
        sectionTimeTable = (MenuSection) findViewById(R.id.menuSectionTimetable);
        sectionNews = (MenuSection) findViewById(R.id.menuSectionNews);
        sectionChat = (MenuSection) findViewById(R.id.menuSectionChat);
        sectionSocial = (MenuSection) findViewById(R.id.menuSectionSocial);

        initSectionButtons();
        initListeners();

        FacebookSdk.sdkInitialize(getApplicationContext());

        if(!PlayerService.isActive() && !PlayerService.isLoading() && !PlayerService.isReady() && PlayerService.isFistRun()) {

            preLoadingTimer=new Timer();
            preLoadingTimer.schedule(new PreLoadingTimerTask(),1000*40);

            serviceLauncher = new PlayerServiceLauncher();
            serviceLauncher.execute(PlayState.BUFFERING);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PlayerService.isActive()) {
            updatePlayerIcon(PlayState.PLAY);
        }
        else if (PlayerService.isLoading()) {
            updatePlayerIcon(PlayState.BUFFERING);
        }
        else {updatePlayerIcon(PlayState.STOP);}
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }


    private void initSectionButtons(){
        sectionPlayer.setmLabel("Ascolta");
        sectionPlayer.setmIcon(R.drawable.play_bn);

        sectionTimeTable.setmIcon(R.drawable.calendar_bn);
        sectionTimeTable.setmLabel("Palinsesto");

        sectionNews.setmIcon(R.drawable.news_bn);
        sectionNews.setmLabel("News");

        sectionChat.setmIcon(R.drawable.chat_bn);
        sectionChat.setmLabel("Chat");

        sectionSocial.setmIcon(R.drawable.megaphone_bn);
        sectionSocial.setmLabel("Social");
    }

    private void initListeners(){
        sectionPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (playerLock) {return;}

                if (!isNetworkConnected(getApplicationContext())) {
                    // notifica
                    return;
                }

                if (!PlayerService.isActive()) {

                    if (PlayerService.isReady()) {
                        updatePlayerIcon(PlayState.PLAY);
                        // avvia
                        PlayerService.setActive(true);
                        PlayerService.startActionStart(getApplicationContext());

                    } else if(PlayerService.isLoading()) {
                        updatePlayerIcon(PlayState.PLAY);
                        PlayerService.setActive(true);
                    }

                    else {
                        playerLock=true;
                        updatePlayerIcon(PlayState.BUFFERING);
                        // inizializza
                        PlayerService.setActive(true);

                        if(serviceLauncher==null || serviceLauncher.getStatus().compareTo(AsyncTask.Status.FINISHED)==0) {
                            serviceLauncher = new PlayerServiceLauncher();
                            serviceLauncher.execute(PlayState.BUFFERING);
                        }
                    }

                } else {
                    updatePlayerIcon(PlayState.STOP);

                    //stoppa
                    PlayerService.startActionStop(getApplicationContext());
                }

                return;
            }
        });

        sectionChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(myIntent);
            }
        });

        sectionTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), ProgramsActivity.class);
                startActivity(myIntent);
            }
        });

        sectionNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), NewsActivity.class);
                startActivity(myIntent);
            }
        });
    }


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        boolean connected = ni != null && ni.isConnectedOrConnecting();

        if (!connected) {
            // There are no active networks.
            return false;
        } else
            return true;
    }


    public static void updatePlayerIcon(PlayState playState) {
        switch (playState) {
            case PLAY:
                sectionPlayer.setmIcon(R.drawable.pause_bn);
                sectionPlayer.setmLabel("premi per fermare");
                break;

            case STOP:
                sectionPlayer.setmIcon(R.drawable.play_bn);
                sectionPlayer.setmLabel("Ascolta");
                break;

            case BUFFERING:
                sectionPlayer.setmIcon(R.drawable.pause_bn);
                sectionPlayer.setmLabel("carico dati....");
                break;
        }

        sectionPlayer.invalidate();
    }


    private class PlayerServiceLauncher extends AsyncTask<PlayState, Void, PlayState>{

        @Override
        protected PlayState doInBackground(PlayState... strings) {
                    PlayerService.initializePlayer();
            return strings[0];
        }

        @Override
        protected void onPostExecute(PlayState state) {
            super.onPostExecute(state);

            if(state.compareTo(PlayState.BUFFERING)==0 && PlayerService.isActive()) {
                updatePlayerIcon(PlayState.PLAY);
            }

            playerLock=false;

        }
    }


    class PreLoadingTimerTask extends TimerTask
    {
        @Override
        public void run() {
            if(!PlayerService.isActive() && (PlayerService.isReady()||PlayerService.isLoading())) {
                PlayerService.resetPlayer();
            }
        }
    }


}
