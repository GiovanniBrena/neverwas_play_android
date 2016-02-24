package com.neverwasradio.neverwasplayer.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.util.BitSet;

/**
 * Created by Giovanni on 23/02/16.
 */
public class FbPost {

    private String text;
    private String date;
    private String id;
    private String[] tag;
    private Bitmap img;
    private String imgUrl;

    private boolean hasImage;

    public FbPost(String id, String text, String date, String imgUrl, String[] tag) {
        this.id=id;
        this.text=text;
        this.date=date;
        this.tag=tag;
        if(imgUrl!=null) {
            hasImage=true;
            this.imgUrl=imgUrl;
        }
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public Bitmap getImg(){
        return img;
    }

    public String[] getTag() {
        return tag;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
