package com.example.a1.mygame.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1.mygame.ActivityLevelEditor;
import com.example.a1.mygame.ActivityLevelMenu;
import com.example.a1.mygame.ActivitySinglePlayer;
import com.example.a1.mygame.ActivityTwoPlayers;
import com.example.a1.mygame.DBHelper;
import com.example.a1.mygame.R;

import mehdi.sakout.fancybuttons.Utils;


public class FragmentTherd extends Fragment {

    public static ListView mylevelsList;
    static Activity example_activity;
    static TextView nolevels;

    // newInstance constructor for creating fragment with arguments
    public static FragmentTherd newInstance(int page, String title, Activity activity) {
        FragmentTherd fragmentTherd = new FragmentTherd();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentTherd.setArguments(args);
        example_activity = activity;
        return fragmentTherd;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_therd, container, false);
        nolevels = (TextView)view.findViewById(R.id.no_levels);
        mylevelsList = (ListView)view.findViewById(R.id.my_level_list);
        mylevelsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(example_activity,"опа",Toast.LENGTH_LONG).show();
                SELECT_OWN_LEVEL(Integer.parseInt(""+id));
            }
        });
        mylevelsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(example_activity, ActivityLevelEditor.class);
                intent.putExtra("id", id);
                startActivity(intent);
                example_activity.finish();
                return true;
            }
        });
        ActivityLevelMenu.updateList();
        return view;
    }
    private void SELECT_OWN_LEVEL(int id){
        Intent intent;
        if (!ActivityLevelMenu.TwoPlayers)
            intent = new Intent(example_activity, ActivitySinglePlayer.class);
        else intent = new Intent(example_activity, ActivityTwoPlayers.class);
        DBHelper dbHelper = ActivityLevelMenu.dbHelperMylvl;
        String sLab = dbHelper.getMAP(id);
        intent.putExtra("level_num", id);
        intent.putExtra("own_level", true);

        ActivityLevelMenu.level_name = dbHelper.getNAME(id);
        ActivityLevelMenu.StartX = dbHelper.getSTARTX(id);
        ActivityLevelMenu.StartY = dbHelper.getSTARTY(id);
        ActivityLevelMenu.EndX = dbHelper.getENDX(id);
        ActivityLevelMenu.EndY = dbHelper.getENDY(id);
        ActivityLevelMenu.level = ActivityLevelMenu.parseLab(sLab, dbHelper.getLINES(id), dbHelper.getCOLUMNS(id));

        startActivity(intent);
        example_activity.finish();
    }
    public static void updateMyLevelList (SimpleCursorAdapter levelAdapter){
        if(mylevelsList!=null) {
            if (levelAdapter.isEmpty())
                nolevels.setVisibility(View.VISIBLE);
            else nolevels.setVisibility(View.INVISIBLE);
            mylevelsList.setAdapter(levelAdapter);
        }
    }
}