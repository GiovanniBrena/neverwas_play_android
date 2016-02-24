package com.neverwasradio.neverwasplayer.UI.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neverwasradio.neverwasplayer.Core.ConnectionHandler;
import com.neverwasradio.neverwasplayer.R;

import java.io.IOException;

public class ChatActivity extends Activity {

    EditText nameField;
    EditText textField;
    LinearLayout sendButton;
    LinearLayout mainLayout;
    LinearLayout sentBanner;
    TextView sentGoBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        nameField = (EditText) findViewById(R.id.chatNameField);
        textField = (EditText) findViewById(R.id.chatTextField);
        sendButton = (LinearLayout) findViewById(R.id.chatSendButton);
        mainLayout = (LinearLayout) findViewById(R.id.chatMainLayout);
        sentBanner = (LinearLayout) findViewById(R.id.chatSentBanner);
        sentGoBack = (TextView) findViewById(R.id.chatSentBack);


        initListeners();

    }

    private void initListeners(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameField.getText().toString().compareTo("")==0) {
                    // insert name
                    Toast.makeText(getApplicationContext(), "Inserisci un nome",
                            Toast.LENGTH_SHORT).show();
                }
                else if(textField.getText().toString().compareTo("")==0) {
                    //insert text
                    Toast.makeText(getApplicationContext(), "Inserisci un messaggio",
                            Toast.LENGTH_SHORT).show();
                }

                else {
                    SendChatMessage sendChatMessage = new SendChatMessage();
                    String[] params = new String[2];
                    params[0] = nameField.getText().toString();
                    params[1] = textField.getText().toString();
                    sendChatMessage.execute(params);

                    mainLayout.setVisibility(View.INVISIBLE);
                    sentBanner.setVisibility(View.VISIBLE);

                    //Toast.makeText(getApplicationContext(), "Messaggio inviato!",
                    //        Toast.LENGTH_LONG).show();

                }
            }
        });

        sentGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private class SendChatMessage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String url = "http://www.mpwebtest.altervista.org/nw-play/send-mex.php";

            String name = params[0];
            String text = params[1];

            name = ConnectionHandler.sanitizeUrlString(name);
            text = ConnectionHandler.sanitizeUrlString(text);

            url = url + "?name=" + name + "&text=" + text;

            try {
                ConnectionHandler.sendMessageToChat(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Executed";
        }
    }


}
