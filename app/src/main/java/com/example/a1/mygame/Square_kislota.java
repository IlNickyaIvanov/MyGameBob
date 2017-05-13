package com.example.a1.mygame;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Square_kislota extends Square {
    Activity main;
    Square_kislota(ActivitySinglePlayer main, float x, float y, int size){
        super(main,x,y,size,2);
        this.main=main;
        square.setImageResource(R.drawable.square_kislota);
        int bubble_num=8;
        if (size<=ActivitySinglePlayer.screenX/2/6)
            bubble_num=6;
        if (size<=ActivitySinglePlayer.screenX/2/10)
            bubble_num=4;
        if (size<=ActivitySinglePlayer.screenX/2/15)
            bubble_num=2;
        Bubble bbls[]=new Bubble[bubble_num];
        for (int i = 0;i<bbls.length;i++) {
            bbls[i] = new Bubble(main,x,y,size,(int)(Math.random()*1000));
        }
    }
    class Bubble{
        ImageView bubble;
        Animation bubbleAnim,zap;
        float mX, mY,sqX,sqY;
        int msize,sqSize;
        Bubble(Activity activity, float x, float y, int size, int startOffset){
            bubble = new ImageView(activity);
            this.sqSize=size;
            this.sqX=x;this.sqY=y;
            msize = (int)(Math.random()*(size/6)+10);
            Restart();
            bubble.setX(mX);
            bubble.setY(mY);
            bubble.setImageResource(R.drawable.bubble);
            activity.addContentView(bubble,new RelativeLayout.LayoutParams(msize,msize));
            bubbleAnim = AnimationUtils.loadAnimation(main,R.anim.bubble);
            bubbleAnim.setStartOffset(startOffset);
            zap = AnimationUtils.loadAnimation(main,R.anim.zap);
            bubbleAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    bubble.setImageResource(R.drawable.zap);
                    bubble.startAnimation(zap);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            zap.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Restart();
                    bubble.setX(mX);
                    bubble.setY(mY);
                    bubble.setImageResource(R.drawable.bubble);
                    bubble.startAnimation(bubbleAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            bubble.startAnimation(bubbleAnim);
        }
        private void Restart(){
            mX =(float) ((Math.random()*sqSize)+sqX-msize);
            mX=(mX<sqX)?sqX+msize:mX;
            mY =(float) ((Math.random()*sqSize)+sqY-msize);
            mY=(mY<sqY)?sqY+msize:mY;

        }
    }
}
