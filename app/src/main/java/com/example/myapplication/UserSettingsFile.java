package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

public class UserSettingsPage extends AppCompatActivity {

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which){
            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    //put the code here
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //put code here
                    break;
            }
        }
    };

    DeleteAccount.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
    .setNegativeButton("No", dialogClickListener).show();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



}