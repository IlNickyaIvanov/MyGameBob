package com.example.a1.mygame.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.a1.mygame.R;


public class FragmentEdit1 extends Fragment {

    public static final String TAG = "OneFragmentTag";
    private EditText editText1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_text1, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        editText1 = (EditText) getActivity().findViewById(R.id.editText1);
    }

    public void SELECTION(int START, int STOP) {
        editText1.setSelection(START, STOP);
    }
    public void SetText(String text){
        editText1.getText().insert(editText1.getSelectionStart(),text);
    }
    public String GetText(){
        return editText1.getText().toString();
    }
    public void clear(){
        editText1.setText("");
    }
}
