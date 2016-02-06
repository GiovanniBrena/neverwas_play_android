package com.neverwasradio.neverwasplayer.UI.Activities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class DayButton extends View {


    private int color=Color.GRAY;
    private boolean selected;
    private String text="text";

    private Paint drawPaint;
    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public DayButton(Context context) {
        super(context);
        init(null, 0);
    }

    public DayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DayButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setColor(int c) {color=c;}
    public boolean isSelected() {return selected;}
    public void setSelected(boolean value) {selected=value;}

    private void init(AttributeSet attrs, int defStyle) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPaint = new Paint();
        mTextPaint = new TextPaint();
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeWidth(8);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


        if(isSelected()) {drawPaint.setColor(color); mTextPaint.setColor(color);}
        else{ drawPaint.setColor(Color.GRAY); mTextPaint.setColor(Color.GRAY);}

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        RectF rect = new RectF(paddingLeft,paddingTop,getWidth()-paddingRight,getHeight()-paddingBottom);
        canvas.drawRoundRect(rect,contentWidth/4,contentHeight/2,drawPaint);

        canvas.drawText(text,getWidth()/2,getHeight()/2,mTextPaint);

    }

}
