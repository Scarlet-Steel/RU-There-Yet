package com.scarletsteel.ruthereyet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class AlarmSettings extends AppCompatActivity {

    public static final String PREFS = "MyPrefsFile";
    TextView title;
    TextView stopText;
    TextView routeText;
    Button finish;
    Context mContext;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_settings);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        routeText = (TextView) findViewById(R.id.route);
        stopText = (TextView) findViewById(R.id.stop);
        title = (TextView) findViewById(R.id.title);

        spinner = (Spinner) findViewById(R.id.menu);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu_options, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        String route = prefs.getString("route", "No route defined");//"No name defined" is the default value.
        routeText.setText(route);
        System.out.println("Set routeText to " + route);
        String stop = prefs.getString("stop", "No stop defined!"); //0 is the default value.
        stopText.setText(stop);
        int titleID = title.getId();


    }


    public void finish(View view) {
        //Save alarm settings
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putString("alarmSettings", "settings");
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

}
