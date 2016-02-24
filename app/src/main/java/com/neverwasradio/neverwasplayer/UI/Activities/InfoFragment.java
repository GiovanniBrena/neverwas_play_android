package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.neverwasradio.neverwasplayer.Core.ConnectionHandler;
import com.neverwasradio.neverwasplayer.Model.NWPost;
import com.neverwasradio.neverwasplayer.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chiara on 13/08/15.
 */
public class InfoFragment extends Fragment {
    InfoFragment thisFragment;

    ImageView fbIcon, twIcon, instaIcon;
    Button websiteButton, emailButton;


    EditText nameField, textField;
    Button sendMessageButton;


    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }
    public InfoFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        websiteButton = (Button) rootView.findViewById(R.id.websiteButton);
        emailButton = (Button) rootView.findViewById(R.id.emailButton);
        fbIcon = (ImageView) rootView.findViewById(R.id.fbIcon);
        twIcon = (ImageView) rootView.findViewById(R.id.twIcon);
        instaIcon = (ImageView) rootView.findViewById(R.id.instaIcon);


        nameField = (EditText) rootView.findViewById(R.id.message_name);
        textField = (EditText) rootView.findViewById(R.id.message_text);
        sendMessageButton = (Button) rootView.findViewById(R.id.message_send);

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

        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.associazionesmart.it/"));
                startActivity(browserIntent);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageInfo pInfo = null;
                try {
                    pInfo = thisFragment.getActivity().getPackageManager().getPackageInfo(thisFragment.getActivity().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;
                String body="Contatto da NeverwasPlay Android " + version;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "info@associazionesmart.it", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, body);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });

        fbIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                try {
                    Context context = thisFragment.getActivity();
                    context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/204344166256803"));
                } catch (Exception e) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/neverwasradio"));
                }
                startActivity(intent);
            }
        });

        twIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    Context context = thisFragment.getActivity();
                    // get the Twitter app if possible
                    context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=895226899"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/neverwasradio"));
                }
                thisFragment.startActivity(intent);

            }
        });

        instaIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/neverwasradio");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/neverwasradio")));
                }
            }
        });




        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendChatMessage sendChatMessage = new SendChatMessage();
                String[] params = new String[2];
                params[0] = nameField.getText().toString();
                params[1] = textField.getText().toString();
                sendChatMessage.execute(params);
            }
        });

    }



    private class SendChatMessage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String url = "http://www.mpwebtest.altervista.org/nw-play/send-mex.php";

            String name = params[0];
            String text = params[1];

            name = sanitizeUrlString(name);
            text = sanitizeUrlString(text);

            url = url + "?name=" + name + "&text=" + text;

            try {
                ConnectionHandler.sendMessageToChat(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Executed";
        }
    }


    private String sanitizeUrlString(String s) {
        s=s.replace("(", "");
        s=s.replace(")", "");
        s=s.replace("[", "");
        s=s.replace("]", "");
        s=s.replace("&", "and");
        s=s.replace("%", "perc");
        s=s.replace("$", "dollar");
        s=s.replace(" ", "%20");
        return s;
    }



}
