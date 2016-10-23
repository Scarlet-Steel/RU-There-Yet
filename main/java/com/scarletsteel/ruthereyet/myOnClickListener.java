package com.scarletsteel.ruthereyet;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Hill on 10/23/2016.
 */
public class myOnClickListener implements View.OnClickListener {

    int input;
    ArrayList<Button> buttonList;
    TextView textField;
    Context context;
    public static final String PREFS = "MyPrefsFile";


    public myOnClickListener(int input, ArrayList<Button> buttonList, TextView textField, Context context) {
        this.input = input;
        this.buttonList = buttonList;
        this.textField = textField;
        this.context = context;
    }

    public void onClick(View v)
    {
        for (int i = 0; i < buttonList.size(); i++) {
            textField.setText(buttonList.get(input).getText());
        }
    }

};