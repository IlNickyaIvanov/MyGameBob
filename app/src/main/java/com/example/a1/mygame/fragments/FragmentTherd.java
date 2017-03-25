package com.example.a1.mygame.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a1.mygame.R;


public class FragmentTherd extends Fragment {

    // newInstance constructor for creating fragment with arguments
    public static FragmentTherd newInstance(int page, String title) {
        FragmentTherd fragmentTherd = new FragmentTherd();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentTherd.setArguments(args);
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
        return view;
    }
}