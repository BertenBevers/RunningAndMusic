package com.example.project_ict_4;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends ActionBarActivity  {
    Button btnStart;
    Button btnSetBPM;
    Button btnDetectRP;
    Button btnCalculator;
    String SongTitle="";
    String Artist="";
    int SongID;
    DatabaseAdapter dbAdapter;
    EchoNestHandler echoservice;

    ArrayList<Song> songList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff1d97dd));
        btnStart = (Button) findViewById(R.id.buttonStart);
        btnSetBPM = (Button) findViewById(R.id.buttonSetBpm);
        btnDetectRP = (Button) findViewById(R.id.btnDetectRunningPace);
        btnCalculator =(Button) findViewById(R.id.btnCalc);
        dbAdapter = new DatabaseAdapter(getApplicationContext());
        btnCalculator.setOnClickListener(new View.OnClickListener()
        {public void onClick(View v) {

                  songList =  getSongList();
                  Collections.reverse(songList);

                       new Thread() {
                            public void run() {
                                try {
                                    for (Song s : songList) {

                                        SongTitle = s.getTitle();
                                        Artist = s.getArtist();
                                        SongID = (int) s.getID();

                                        echoservice = new EchoNestHandler();
                                        String Url = echoservice.FormatUrl(Artist, SongTitle);
                                        double bpm = echoservice.SendToNest(Url);

                                        dbAdapter.insertData(SongTitle,Artist,bpm,SongID);
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

                btnSetBPM.setText(dbAdapter.getAllData());
            }
        });
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
