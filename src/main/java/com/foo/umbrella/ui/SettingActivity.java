package com.foo.umbrella.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.foo.umbrella.R;

/**
 * Created by Administrator on 2017/10/3.
 */

public class SettingActivity extends AppCompatActivity {
    Toolbar toolbar;
    public EditText units, zip;
    final CharSequence[] charSequences = {"Fahrenheit", "Celsius"};
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingdialog);
        editor = getSharedPreferences("umbrella", MODE_PRIVATE).edit();
        zip = (EditText) findViewById(R.id.zip);
        zip.setInputType(InputType.TYPE_CLASS_NUMBER);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        units = (EditText)findViewById(R.id.units);
        units.setFocusable(false);
        units.setClickable(true);
        units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setSingleChoiceItems(charSequences, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        units.setText(charSequences[which]);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        if(zip != null){
            editor.putString("location", zip.getText().toString());
            editor.apply();
        }
        onBackPressed();
        return true;
    }
}
