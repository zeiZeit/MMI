package com.wind.emode.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.wind.factoryautotest.R;

public class MyComplexView extends MyView {
    private Rectangle[] mRectangles;
    private SlashShape mSlashShape;
    private Context mActivity;
    private boolean mHasSlash;
    
    private Path mPath;
    private Paint mPathPaint;
    
    public MyComplexView(Context c){
        super(c);
        int w = mScreenWidth;
        int h = mScreenHeight/* + getResources().getInteger(R.integer.navegation_height)*/;
        int wp = getResources().getInteger(R.integer.config_tp_test_rect_section_width);
        int hp = getResources().getInteger(R.integer.config_tp_test_rect_section_height);
        float uw = w*1.0f/wp;
        float uh = h*1.0f/hp;
        mRectangles = new Rectangle[2];
        mRectangles[0] = new Rectangle(wp, hp, (int)(uw*(wp)), (int)(uh*(hp)), 0, 0);
	    mRectangles[1] = new Rectangle((wp-2)/2, (hp-2)/2, (int)(uw*(wp-2)), (int)(uh*(hp-2)), (int)uw, (int)uh);
        mHasSlash = getResources().getBoolean(R.bool.config_tp_test_has_slash);
        if(mHasSlash){
            int offsetX = (int)(2*uw);
            int offsetY = (int)(2*uh);
            mSlashShape = new SlashShape(w - offsetX*2, h - offsetY*2, 26, offsetX, offsetY);
        }
        
        mPath = new Path();
        mPathPaint = new Paint();
        mPathPaint.setColor(Color.RED);
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int len = mRectangles.length;
        for(int i = 0; i < len; i ++){
            mRectangles[i].processTouchEvent(event);
        }
        if(mHasSlash){
            mSlashShape.processTouchEvent(event);
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDown(x, y);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                touchMove(x, y);
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                touchUp(x, y);
                break;
            }
        }

        invalidate();
        if(event.getAction() == MotionEvent.ACTION_UP){
            boolean flag = true;
            for(int i = 0; i < len; i ++){
                if(!mRectangles[i].isAllTouch()){
                    flag = false;
                    break;
                }
            }
            if(flag && mHasSlash){
                flag &= mSlashShape.isAllTouch();
            }
            if(flag){
               if (mListener != null) {
                   mListener.onTestFinished(mTestStage);
               }
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int len = mRectangles.length;
        for(int i = 0; i < len; i ++){
            mRectangles[i].draw(canvas);
        }
        if(mHasSlash){
            mSlashShape.draw(canvas);
        }
        
        canvas.save();
        canvas.drawPath(mPath, mPathPaint);
        canvas.restore();
    }
    
    private void touchDown(float x, float y) {
        mPath.moveTo(x, y);
    }

    private void touchMove(float x, float y) {
        mPath.lineTo(x, y);
    }

    private void touchUp(float x, float y) {
        mPath.lineTo(x, y);
    }
}
