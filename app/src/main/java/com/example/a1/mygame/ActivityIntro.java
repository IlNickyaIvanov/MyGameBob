package com.example.a1.mygame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//активность - интро
public class ActivityIntro extends AppCompatActivity {
    ImageView intro, eye;
    Animation introAnim;
    AnimationDrawable anim_eye;
    Intent intent;
    int screenWidth, screenHeight;
    static boolean isIntro = true;
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        intent = new Intent(ActivityIntro.this, ActivityMain.class);
        //получение данных о старте интро или его пропуске
        try {
            mSettings = getSharedPreferences(ActivitySettings.APP_PREFERENCES, Context.MODE_PRIVATE);
            isIntro = !mSettings.contains(ActivitySettings.APP_PREFERENCES_INTRO) || mSettings.getBoolean(ActivitySettings.APP_PREFERENCES_INTRO, true);
        } catch (Throwable t) {
            isIntro = true;
        }

        //само интро
        if (isIntro) {
            intro = new ImageView(this);
            eye = new ImageView(this);
            this.addContentView(intro, new RelativeLayout.LayoutParams(screenWidth, screenHeight));
            this.addContentView(eye, new RelativeLayout.LayoutParams(screenWidth / 3, screenWidth / 3));
            intro.setX(0);
            intro.setY(0);
            intro.setImageResource(R.drawable.intro);
            eye.setX((screenWidth - screenWidth / 3) / 2);
            eye.setY((screenHeight - screenWidth / 3) / 2);
            introAnim = AnimationUtils.loadAnimation(this, R.anim.intro);
            eye.setBackgroundResource(R.drawable.eye_anim);
            anim_eye = (AnimationDrawable) eye.getBackground();
            anim_eye.start();
            eye.startAnimation(introAnim);
            intro.startAnimation(introAnim);
            introAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    eye.setVisibility(View.INVISIBLE);
                    intro.setVisibility(View.INVISIBLE);
                    startActivity(intent);
                    ActivityIntro.this.finish();
                    anim_eye.stop();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            //старт основной активности
            startActivity(intent);
            this.finish();
        }
    }
}
