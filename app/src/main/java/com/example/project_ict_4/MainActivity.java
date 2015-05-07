package com.example.project_ict_4;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity {
    Button btnStart;
    Button btnSetBPM;
    Button btnDetectRP;
    Button btnCalculator;
    String SongTitle;
    String Artist;
    EchoNestHandler echoservice;

    //Make a collection for the accelormeter data so we can use it for the pace recognition

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff1d97dd));
        btnStart = (Button) findViewById(R.id.buttonStart);
        btnSetBPM = (Button) findViewById(R.id.buttonSetBpm);
        btnDetectRP = (Button) findViewById(R.id.btnDetectRunningPace);
        btnCalculator =(Button) findViewById(R.id.btnCalc);

        btnCalculator.setOnClickListener(new View.OnClickListener()
        {public void onClick(View v) {
                echoservice = new EchoNestHandler();
                    for (Song s : player.songList) {
                        SongTitle=s.getTitle();
                        Artist=s.getArtist();

                        String Url =echoservice.FormatUrl(Artist,SongTitle);
                        double bpm= echoservice.SendToNest(Url);
                        String bpm2 = String.valueOf(bpm);
                        Toast.makeText(MainActivity.this,bpm2, Toast.LENGTH_LONG).show();

                    }
            }});



        btnDetectRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StepResponse.class);
                startActivity(i);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(getApplicationContext(), player.class);
               startActivity(i);
            }
        });
        btnSetBPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // vind hoe je nieuw scherm kan maken voor layout van SetBpm.
                // of slider implementeren incrementatie van 1bpm.

                //vervolgens liedjes aan bpm linken.
            }
        });
    }
    public short SetBPM(short bpm)
    {
        return bpm;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


}
