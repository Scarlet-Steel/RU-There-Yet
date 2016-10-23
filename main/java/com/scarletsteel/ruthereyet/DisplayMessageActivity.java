package com.scarletsteel.ruthereyet;

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
import android.widget.TextView;


public class DisplayMessageActivity extends AppCompatActivity {

    public static final String PREFS = "MyPrefsFile";
    TextView stopText;
    TextView routeText;
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);


        ViewGroup layout = (ViewGroup) findViewById(R.id.content_display_message);
        layout.addView(textView);

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

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

}
