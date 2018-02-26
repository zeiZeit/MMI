package com.wind.factoryautotest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.widget.Button;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.PixelFormat;

public class SurfaceTest extends Activity {

    /*private Surface mSurface = null;
    private SurfaceControl mSurfaceControl = null;

    private void drawColor(){
        SurfaceControl.openTransaction();
        try {
            try {
                mSurfaceControl.setPosition(0, 0);
                Canvas canvas = mSurface.lockCanvas(null);
                try {
                    //canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    canvas.drawColor(Color.WHITE);
                    android.util.Log.i("yangjiajun", "draw color end0.");
                } finally {
                    mSurface.unlockCanvasAndPost(canvas);
                }
                mSurfaceControl.setLayer(999);
                mSurfaceControl.setAlpha(0);
                mSurfaceControl.show();
                android.util.Log.i("yangjiajun", "draw color end.");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } finally {
            SurfaceControl.closeTransaction();
        }
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mSurfaceControl = new SurfaceControl(new SurfaceSession(), "lcd_test", 720, 1280, PixelFormat.RGB_888, SurfaceControl.HIDDEN);
        //mSurface = mSurfaceControl.getSurface();
        Button btn = new Button(this);
        btn.setText("test");
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                android.util.Log.i("yangjiajun", "draw color begin.");
                //drawColor();
            }
        });
        setContentView(btn);
    }
}