package com.example.a1.mygame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class ActivitySettings extends Activity {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_INTRO = "intro";
    public static SharedPreferences mSettings;
    Switch intro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getWindow().setBackgroundDrawableResource(R.drawable.background);
        }else{
            getWindow().setBackgroundDrawableResource(R.drawable.backgroundvertical);
        }

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        intro = (Switch) findViewById(R.id.intro);
        if (mSettings.contains(ActivitySettings.APP_PREFERENCES_INTRO)) {
            intro.setChecked(mSettings.getBoolean(ActivitySettings.APP_PREFERENCES_INTRO, true));
        }
        else setSettings(1);
        intro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               setSettings(1);
            }
        });
    }

    void setSettings(int SharedType){
        SharedPreferences.Editor editor = mSettings.edit();
        switch (SharedType){
            case (1):
                editor.putBoolean(APP_PREFERENCES_INTRO, intro.isChecked());
                break;
            default: break;
        }
        editor.apply();
    }
}