package com.example.a1.mygame;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class ActivityHowToPlay extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howtoplay);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getWindow().setBackgroundDrawableResource(R.drawable.background);
        }else{
            getWindow().setBackgroundDrawableResource(R.drawable.backgroundvertical);
        }
    }
}