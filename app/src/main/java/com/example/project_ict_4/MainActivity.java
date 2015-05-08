package com.example.project_ict_4;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity {
    Button btnStart;
    Button btnSetBPM;
    Button btnDetectRP;
    Button btnCalculator;
    String SongTitle="";
    String Artist="";
    EchoNestHandler echoservice;
    String bpm2;
    ArrayList<Song> songList = new ArrayList<>();



    Song s1 = new Song(1,"Insomnia", "Faithless"); //127.005
    Song s2 = new Song(2,"Music is my alibi", "Mark with a k"); //150.069
    Song s3 = new Song(3,"Promesses", "tchami"); //124.01
    Song s4 = new Song(4,"Raise Your Fist", "Angerfist");




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
        final Handler handler = new Handler();
        btnCalculator.setOnClickListener(new View.OnClickListener()
        {public void onClick(View v) {


                /*songList.add(s1);
                songList.add(s2);
                songList.add(s3);
                songList.add(s4);*/

                  songList =  getSongList();
                Collections.reverse(songList);



                       new Thread() {
                            public void run() {
                                try {
                                    for (Song s : songList) {
                                        SongTitle = s.getTitle();
                                        Artist = s.getArtist();
                                     // if(SongTitle == "Promesses") {

                                            echoservice = new EchoNestHandler();
                                            String Url = echoservice.FormatUrl(Artist, SongTitle);
                                            double bpm = echoservice.SendToNest(Url);
                                            bpm2 = String.valueOf(bpm);

                                            handler.post(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(MainActivity.this,bpm2, Toast.LENGTH_LONG).show();

                                                }

                                            });

                                      // }
                                        }


                                    }catch(Exception v){
                                        System.out.println(v);
                                    }

                            }
                        }.start();

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

    public ArrayList<Song> getSongList() {

        ArrayList<Song> list = new ArrayList<>();

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);


        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);


                list.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }

            return list;
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
