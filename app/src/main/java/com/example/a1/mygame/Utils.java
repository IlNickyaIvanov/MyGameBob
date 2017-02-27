package com.example.a1.mygame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

public class Utils {
    public static void makeToast(Activity main,String text){
        Toast.makeText(main,text,Toast.LENGTH_LONG).show();
    }
    public static void AlertDialog(Activity main, String title, String Message, String TextButton ){
        MyGame.AlertDialogMessage=null;

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        AlertDialogShow(builder,title,Message,TextButton);
    }
    private static void AlertDialogShow(AlertDialog.Builder builder,String title, String Message, String TextButton ){
        builder.setTitle(title)
                .setMessage(Message)
                .setCancelable(false)
                .setNegativeButton(TextButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
