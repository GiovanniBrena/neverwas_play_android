package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.neverwasradio.neverwasplayer.R;

public class PostActivity extends Activity {

    final String mimeType = "text/html";
    final String encoding = "UTF-8";

    private String title;
    private String date;
    private String content;

    private TextView titleLabel;
    private TextView dateLabel;
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        Bundle b = getIntent().getExtras();
        title = b.getString("title");
        date = b.getString("date");
        content = b.getString("content");

        titleLabel = (TextView) findViewById(R.id.postDetailTitle);
        dateLabel = (TextView) findViewById(R.id.postDetailDate);
        webView = (WebView) findViewById(R.id.postDetailWebView);

        titleLabel.setText(title);
        dateLabel.setText(date);

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadDataWithBaseURL("", content, mimeType, encoding, "");


    }

}
