package com.example.a1.mygame.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.a1.mygame.R;


public class FragmentEdit2 extends Fragment {

    public static final String TAG = "TwoFragmentTag";
    private EditText editText2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_text2, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        editText2 = (EditText) getActivity().findViewById(R.id.editText2);
    }

    public void SELECTION(int START, int STOP) {
        editText2.setSelection(START, STOP);
    }
    public void SetText(String text){
        editText2.getText().insert(editText2.getSelectionStart(),text);
    }
    public String GetText(){
        return editText2.getText().toString();
    }
    public void clear(){
        editText2.setText("");
    }
}
