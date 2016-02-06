package com.neverwasradio.neverwasplayer.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Giovanni on 21/01/16.
 */
public class NWPost {

    String title;
    String date;
    String content;


    public NWPost(JSONObject json){

        try {
            title = json.getJSONObject("title").getString("rendered");
            date = json.getString("date");
            content = json.getJSONObject("content").getString("rendered");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
