package com.neverwasradio.neverwasplayer.UI.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.DimenRes;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.View;

import com.neverwasradio.neverwasplayer.R;

/**
 * Created by Giovanni on 12/02/16.
 */
public class CustomMenuTab extends View {

    int backgroundColor = Color.argb(255,128,128,128);
    int textColor = Color.argb(255,255,255,255);

    boolean selected=false;

    Paint paint;
    TextPaint textPaint;

    String title;


    public CustomMenuTab(Context context, int index, String text) {
        super(context);

        paint=new Paint();
        textPaint=new TextPaint();
        title=text;

        if(index==0) { setTabSelected(true);}

    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTabSelected(boolean value){
        selected=value;
        if(value) {backgroundColor=Color.argb(255, 51, 153, 255);}
        else {backgroundColor=Color.argb(255,128,128,128);};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(getBackgroundColor());
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        /*
        if(selected) {
            paint.setColor(Color.WHITE);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(getWidth()/3,getHeight());
            path.lineTo(getWidth()/ 2,getHeight()/3*2);
            path.lineTo(getWidth()/3*2,getHeight());
            path.lineTo(getWidth()/3,getHeight());
            path.close();

            canvas.drawPath(path, paint);
        }
*/
        textPaint.setColor(getTextColor());
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(title, getWidth() / 2, getHeight() / 2, textPaint);

    }
}
