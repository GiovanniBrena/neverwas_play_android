package com.neverwasradio.neverwasplayer.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.neverwasradio.neverwasplayer.Core.FbPostParser;
import com.neverwasradio.neverwasplayer.Model.FbPost;
import com.neverwasradio.neverwasplayer.Model.FbPostManager;
import com.neverwasradio.neverwasplayer.Model.NWProgram;
import com.neverwasradio.neverwasplayer.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsActivity extends Activity {

    AccessToken token;
    ArrayList<FbPost> posts;

    LoginButton loginButton;
    CallbackManager callbackManager;

    RelativeLayout loginLayout, progressLayout, postListLayout;
    LinearLayout mainLayout;

    ListView postListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (LinearLayout) findViewById(R.id.newsMainLayout);
        loginLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        progressLayout = (RelativeLayout) findViewById(R.id.newsProgressLayout);
        postListLayout = (RelativeLayout) findViewById(R.id.newsListLayout);
        postListView = (ListView) findViewById(R.id.newsListView);

        token = AccessToken.getCurrentAccessToken();
        posts = new ArrayList<FbPost>();

        if(token!=null) {
            loginLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);

            if(FbPostManager.getInstance()==null) {
                GetPostsAsyncTask getPostsAsyncTask = new GetPostsAsyncTask();
                getPostsAsyncTask.execute();
            }
            else {
                postListView.setAdapter(new NewsListAdapter(getApplicationContext(), R.layout.item_post_list,FbPostManager.getAllPosts()));
            }
        }
        else {
            mainLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
        }

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");

        // Callback registration
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.e("Facebook-SDK", "login successful");
                AccessToken accessToken = loginResult.getAccessToken();
                Log.e("FB token", accessToken.getToken());
                Log.e("FB user id", accessToken.getUserId());
                Log.e("FB app id", accessToken.getApplicationId());

                token = AccessToken.getCurrentAccessToken();
                if(FbPostManager.getInstance()==null) {
                    GetPostsAsyncTask getPostsAsyncTask = new GetPostsAsyncTask();
                    getPostsAsyncTask.execute();
                }
                else {
                    postListView.setAdapter(new NewsListAdapter(getApplicationContext(), R.layout.item_post_list,FbPostManager.getAllPosts()));
                }

            }
            @Override
            public void onCancel() {
                // App code
                Log.e("Facebook-SDK", "login canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    mainLayout.setVisibility(View.GONE);
                    loginLayout.setVisibility(View.VISIBLE);

                    token = null;
                }

                else {
                    loginLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);

                    token = AccessToken.getCurrentAccessToken();
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public class GetPostsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            postListLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            Log.e("FB POSTS", "begin fetch posts");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FbPostManager.create(token);
            FbPostManager.fetchFacebookPosts();

            //FbPostManager.downloadImages();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.e("FB POSTS", "fetch posts completed");

            ArrayList<FbPost> posts = FbPostManager.getAllPosts();
            for (int i=0; i<posts.size(); i++) {
                DownloadImagesTask downloadImagesTask = new DownloadImagesTask();
                downloadImagesTask.execute(posts.get(i));
            }

            //postListView.setAdapter(new NewsListAdapter(getApplicationContext(), R.layout.item_post_list, FbPostManager.getAllPosts()));
            //progressLayout.setVisibility(View.GONE);
            //postListLayout.setVisibility(View.VISIBLE);

        }
    }

    private class DownloadImagesTask extends AsyncTask<FbPost, Void, Boolean> {

        @Override
        protected Boolean doInBackground(FbPost... fbPosts) {
            return FbPostManager.downloadPostImage(fbPosts[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) { FbPostManager.imagesDownloadCounter++;}
            else {FbPostManager.totalImages--;}

            Log.e("IMG Dowbload", "image downloaded, remaining: "+String.valueOf(FbPostManager.totalImages - FbPostManager.imagesDownloadCounter));

            if(FbPostManager.imagesDownloadCounter==FbPostManager.totalImages) {
                postListView.setAdapter(new NewsListAdapter(getApplicationContext(), R.layout.item_post_list, FbPostManager.getAllPosts()));
                progressLayout.setVisibility(View.GONE);
                postListLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class NewsListAdapter extends ArrayAdapter<FbPost> {

        ArrayList<FbPost> programs;

        public NewsListAdapter(Context context, int resource, ArrayList<FbPost> objects) {
            super(context, resource, objects);
            programs=objects;
        }

        @Override
        public int getCount()
        {
            return programs.size();
        }

        @Override
        public FbPost getItem(int position)
        {
            return programs.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            if (v==null)
            {
                v= LayoutInflater.from(getContext()).inflate(R.layout.item_post_list, null);
            }

            FbPost p=(FbPost) getItem(position);

            TextView label=(TextView) v.findViewById(R.id.postItemDateLabel);
            label.setText(p.getDate());
            label.setVisibility(View.GONE);

            label=(TextView) v.findViewById(R.id.postItemTextLabel);
            label.setText(p.getText());

            label=(TextView) v.findViewById(R.id.postItemTagLabel);
            String s="Con ";

            if(p.getTag()!=null) {
                for (int i = 0; i < p.getTag().length; i++) {
                    if(i==p.getTag().length-1) {s = s + p.getTag()[i];}
                    else {s = s + p.getTag()[i] + ", ";}
                }
                label.setText(s);
            }
            else {label.setText("");}

            ImageView icon = (ImageView) v.findViewById(R.id.postItemImage);
            icon.setImageBitmap(p.getImg());
            return v;
        }
    }

}
