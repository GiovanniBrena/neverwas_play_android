package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.neverwasradio.neverwasplayer.Core.PlayerService;
import com.neverwasradio.neverwasplayer.R;

import java.util.Timer;
import java.util.TimerTask;


public class PlayerFragment extends Fragment {

    PlayerFragment thisFragment;

    NWPlayerButton playerButton;
    TextView playerInfo;
    RelativeLayout progressLayout;
    ProgressBar progressBar2;

    private Timer timeoutTimer;
    TimeoutBufferTimerTask timeoutBufferTimerTask;

    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        return fragment;
    }
    public PlayerFragment() {
        // Required empty public constructor
        thisFragment=this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PlayerService.initializePlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        playerButton = (NWPlayerButton) rootView.findViewById(R.id.playerButton);
        playerInfo = (TextView) rootView.findViewById(R.id.playerInfo);
        progressLayout = (RelativeLayout) rootView.findViewById(R.id.loadingLayout);
        progressBar2 = (ProgressBar) rootView.findViewById(R.id.progressBar2);

        //playerInfo.setText("Premi per ascoltare!");

        playerButton.setMainActivity(getActivity());
        timeoutTimer = new Timer();

        initListeners();

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initListeners(){
        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PlayerService.isActive()) {

                    timeoutBufferTimerTask=new TimeoutBufferTimerTask();
                    timeoutTimer.schedule(timeoutBufferTimerTask,20000);

                    if(MainActivity.isNetworkConnected(getActivity())) {
                        if (PlayerService.getPlayerFragment() == null) {
                            PlayerService.setPlayerFragment(thisFragment);
                        }
                        setLoadingButton();
                        PlayerService.initializePlayer();

                        PlayerService.startActionStart(getActivity());
                    }
                    else {
                        playerInfo.setText("Oops...\nsembra che tu non sia connesso alla rete!");
                        playerInfo.setTextColor(Color.RED);
                    }
                }
                else {
                    setPlayButton();
                    PlayerService.startActionStop(getActivity());
                    sendAnalyticsPlayEvent();
                }
            }
        });

    }

    public void setPauseButton(){
        playerButton.setMode(NWPlayerButton.PAUSE);
        progressLayout.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
        playerInfo.setText("Premi per fermare");
        playerInfo.setTextColor(Color.BLACK);
    }

    public void setPlayButton(){
        playerButton.setMode(NWPlayerButton.PLAY);
        progressLayout.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
        playerInfo.setText("Premi per ascoltare!");
        playerInfo.setTextColor(Color.BLACK);
    }

    public void setLoadingButton(){
        playerButton.setMode(NWPlayerButton.BLANK);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.VISIBLE);
        playerInfo.setText("Carico...");
        playerInfo.setTextColor(Color.BLACK);
    }

    public void showErrorConnection(){
        setPlayButton();
        playerInfo.setText("Ooops...\nc'è un problema con il server, riprova più tardi!");
        playerInfo.setTextColor(Color.RED);
    }

    private class TimeoutBufferTimerTask extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorConnection();
                }
            });
        }
    }

    public void stopTimeoutTask(){
        if(timeoutBufferTimerTask!=null) {
            timeoutBufferTimerTask.cancel();
        }
    }

    private void sendAnalyticsPlayEvent(){
        EasyTracker easyTracker = EasyTracker.getInstance(getActivity());

        // MapBuilder.createEvent().build() returns a Map of event fields and values
        // that are set and sent with the hit.
        easyTracker.send(MapBuilder
                        .createEvent("ui_action",     // Event category (required)
                                "PLAY",  // Event action (required)
                                "PLAY",   // Event label
                                null)            // Event value
                        .build()
        );

    }

}
