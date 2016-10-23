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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class DisplayMessageActivity extends AppCompatActivity {

    public static final String PREFS = "MyPrefsFile";
    TextView title;
    TextView stopText;
    TextView routeText;
    Button stopButton;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_display_message);
        mContext = this;

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        title = (TextView) findViewById(R.id.title);


        ViewGroup layout = (ViewGroup) findViewById(R.id.content_display_message);
        //layout.addView(textView);

        stopText = (TextView) findViewById(R.id.stop);
        routeText = (TextView) findViewById(R.id.route);
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String route = prefs.getString("route", "No route defined");//"No name defined" is the default value.
        routeText.setText(route);
        stopButton = (Button) findViewById(R.id.stopButton);
    }

    public void setStop(View view) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putString("stop", stopText.getText().toString());
        System.out.println("set stop to " + stopText.getText().toString());
        editor.commit();

        Intent intent = new Intent(this, AlarmSettings.class);
        startActivity(intent);

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

        final ArrayList<String> stopList = getStops();
        final ArrayList<Button> stopButtons = new ArrayList<Button>();

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_display_message);

        for (int i = 0; i < stopList.size(); i++) {
            Button temp = new Button(this);
            temp.setText(stopList.get(i));
            temp.setId(i + 1);

            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                params.addRule(RelativeLayout.BELOW, titleID);
            } else {
                params.addRule(RelativeLayout.BELOW, i);
            }
            temp.setLayoutParams(params);
            stopButtons.add(temp);

            temp.setOnClickListener(new myOnClickListener(i, stopButtons, stopText, mContext) {

            });

            layout.addView(temp);
        }
    }

    public ArrayList<String> getStops() {
        ArrayList<String> stopList = new ArrayList<>();
        stopList.add("Stop 1");
        stopList.add("Stop 2");
        stopList.add("Stop 3");
        return stopList;
    }

}
