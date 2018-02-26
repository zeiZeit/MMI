package com.wind.factoryautotest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MyComplexView extends View {
    private Rectangle[] mRectangles;
    //private SlashShape mSlashShape;
    Activity mActivity;
    public MyComplexView(Context c){
        super(c);
        DisplayMetrics dm = new DisplayMetrics();
        mActivity = (Activity)c;
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels + 94;
        Log.i("yangjiajun", "h: " + h);
        int wp = getResources().getInteger(R.integer.tp_line_width_section);
        int hp = getResources().getInteger(R.integer.tp_line_height_section);
        float uw = w*1.0f/wp;
        float uh = h*1.0f/hp;
        //int cs = 3;
        mRectangles = new Rectangle[1];
        mRectangles[0] = new Rectangle(wp, hp, (int)(uw*(wp)), (int)(uh*(hp)), 1, 1);
        //mRectangles[1] = new Rectangle((wp-2)/2, (hp-2)/2, (int)(uw*(wp-2)), (int)(uh*(hp-2)), (int)uw, (int)uh);
        /*for(int i = 0; i < cs; i ++){
            mRectangles[i] = new Rectangle(wp-i*2, hp-i*2, (int)(uw*(wp-i*2)), (int)(uh*(hp-i*2)), (int)uw*i, (int)uh*i);
        };*/
        //int offsetX = (int)(2*uw);
        //int offsetY = (int)(2*uh);
        //mSlashShape = new SlashShape(w - offsetX*2, h - offsetY*2, 26, offsetX, offsetY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int len = mRectangles.length;
        for(int i = 0; i < len; i ++){
            mRectangles[i].processTouchEvent(event);
        }
        //mSlashShape.processTouchEvent(event);
        invalidate();
        /*if(event.getAction() == MotionEvent.ACTION_UP){
            boolean flag = true;
            for(int i = 0; i < len; i ++){
                if(!mRectangles[i].isAllTouch()){
                //mActivity.btn_pass.callOnClick();
                //finish();
                    flag = false;
                    break;
                }
            }
            if(flag && mSlashShape.isAllTouch()){
                Toast.makeText(getContext(), "--ok--", Toast.LENGTH_LONG).show();
            }
        }*/
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int len = mRectangles.length;
        for(int i = 0; i < len; i ++){
            mRectangles[i].draw(canvas);
            //mSlashShape.draw(canvas);
        }
    }
    
    public boolean isAllTouch(){
        boolean flag = true;
        int len = mRectangles.length;
        for(int i = 0; i < len; i ++){
            if(!mRectangles[i].isAllTouch()){
                flag = false;
                break;
            }
        }
        return flag/* && mSlashShape.isAllTouch()*/;
    }
}
