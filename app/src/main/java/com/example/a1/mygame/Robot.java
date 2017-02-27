package com.example.a1.mygame;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Robot {
    float x,y;
    int size;
    int screenX,screenY;
    ImageView body,eye;
    AnimationDrawable anim_body,anim_eye;
    TranslateAnimation TranslateAnimation;
    Robot (Activity main, float x, float y, int size,int screenX,int screenY){
        body = new ImageView(main);
        eye = new ImageView(main);
        this.x=x;this.y=y;this.size=size;this.screenX=screenX;this.screenY=screenY;
        main.addContentView(body,new RelativeLayout.LayoutParams(size,size));
        main.addContentView(eye,new RelativeLayout.LayoutParams(size,size));
        CreateAnim ();
    }
    private void CreateAnim (){
        body.setX(x);eye.setX(x);
        body.setY(y);eye.setY(y);
        body.setBackgroundResource(R.drawable.body_anim);
        eye.setBackgroundResource(R.drawable.eye_anim);
        anim_body = (AnimationDrawable)body.getBackground();
        anim_eye = (AnimationDrawable)eye.getBackground();
        anim_body.start();
        anim_eye.start();
    }

    public void RobotMove (float y, float x){
        TranslateAnimation = new TranslateAnimation(this.x-screenX/61,x-screenX/61,this.y-screenY/17,y-screenY/17);
        TranslateAnimation.setDuration(MyGame.onTick);
        TranslateAnimation.setFillAfter(true);
        body.startAnimation(TranslateAnimation);
        eye.startAnimation(TranslateAnimation);
        this.x=x; this.y=y;
    }
}