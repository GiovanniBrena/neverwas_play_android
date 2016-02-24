package com.neverwasradio.neverwasplayer.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.neverwasradio.neverwasplayer.Core.FbPostParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Giovanni on 23/02/16.
 */
public class FbPostManager {

    private static AccessToken token;

    private static FbPostManager instance;
    private static ArrayList<FbPost> postList;

    public static int imagesDownloadCounter = 0;
    public static int totalImages = 10;

    public static void create(AccessToken t){
        postList=new ArrayList<FbPost>();
        instance=new FbPostManager();
        token=t;
    }

    public static FbPostManager getInstance() {
        return instance;
    }

    public static void fetchFacebookPosts() {

        final int[] postCounter = new int[1];

        if(instance==null) { return; }

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/204344166256803/posts",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                     /* handle the result */
                        JSONArray postArray = null;
                        try {
                            postArray = response.getJSONObject().getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        int postLimit = 10;
                        if(postArray.length()<10) {postLimit=postArray.length();}

                        for (int i=0; i<postLimit; i++) {
                            String postId;
                            try {
                                postId = postArray.getJSONObject(i).getString("id");

                                final int finalI = i;
                                final JSONArray finalPostArray = postArray;
                                new GraphRequest(
                                        AccessToken.getCurrentAccessToken(),
                                        "/"+postId+"/attachments",
                                        null,
                                        HttpMethod.GET,
                                        new GraphRequest.Callback() {
                                            public void onCompleted(GraphResponse response) {
                                             /* handle the result */
                                                try {
                                                    FbPost p = FbPostParser.parseFbPostResponse(finalPostArray.getJSONObject(finalI), response.getJSONObject().getJSONArray("data"));
                                                    if(p!=null) { postList.add(p); }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                ).executeAndWait();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAndWait();
    }

    public static boolean downloadPostImage(FbPost post) {
        try {
            InputStream in = new java.net.URL(post.getImgUrl()).openStream();
            post.setImg(BitmapFactory.decodeStream(in));
        } catch (Exception e) {
            e.printStackTrace();
            post.setHasImage(false);
            return false;
        }
        return true;
    }

    public static void downloadImages() {
        for(int i=0; i<postList.size(); i++) {
            try {
                InputStream in = new java.net.URL(postList.get(i).getImgUrl()).openStream();
                postList.get(i).setImg(BitmapFactory.decodeStream(in));
            } catch (Exception e) {
                e.printStackTrace();
                postList.get(i).setHasImage(false);
            }
        }
    }

    public static ArrayList<FbPost> getAllPosts() { return postList; }



}
