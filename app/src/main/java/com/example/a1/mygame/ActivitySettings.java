package com.example.a1.mygame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

//это класс настроек, тут есть редактор Шаредов
public class ActivitySettings extends Activity {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_INTRO = "intro";
    public static final String APP_PREFERENCES_MINIGAME = "minigame";
    public static SharedPreferences mSettings;
    Switch intro;
    EditText miniGameStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //установка обоев
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setBackgroundDrawableResource(R.drawable.background);
        } else {
            getWindow().setBackgroundDrawableResource(R.drawable.backgroundvertical);
        }

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        intro = (Switch) findViewById(R.id.intro);
        miniGameStart = (EditText) findViewById(R.id.miniGameStart);
        //получение данных и установка соответсвующих значений
        if (mSettings.contains(ActivitySettings.APP_PREFERENCES_INTRO)) {
            intro.setChecked(mSettings.getBoolean(ActivitySettings.APP_PREFERENCES_INTRO, true));
        } else setSettings(1);
        if (mSettings.contains(ActivitySettings.APP_PREFERENCES_MINIGAME)) {
            miniGameStart.setText(mSettings.getInt(ActivitySettings.APP_PREFERENCES_MINIGAME, 10) + "");
        } else setSettings(2);
        intro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSettings(1);
            }
        });
    }

    //универсальный метод установки значений
    void setSettings(int SharedType) {
        SharedPreferences.Editor editor = mSettings.edit();
        switch (SharedType) {
            case (1):
                editor.putBoolean(APP_PREFERENCES_INTRO, intro.isChecked());
                break;
            case (2):
                String text = miniGameStart.getText().toString();
                if (!text.equals("") && !text.equals("0"))
                    editor.putInt(APP_PREFERENCES_MINIGAME, Integer.parseInt(miniGameStart.getText().toString()));
                break;
            default:
                break;
        }
        editor.apply();
    }

    public void apply(View view) {
        setSettings(2);
        Intent intent = new Intent(ActivitySettings.this, ActivityMain.class);
        startActivity(intent);
        this.finish();
    }
}