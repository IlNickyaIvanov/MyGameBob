package com.example.a1.mygame;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
//это активность с правилами игры
//(сам текст в res/values/string)
public class ActivityHowToPlay extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howtoplay);
    }
}