package com.example.project_ict_4;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class StepResponse extends Activity implements SensorEventListener {
    private SensorManager sensorManager;

/*    private TextView xCoor; // declare X axis object
    private TextView yCoor; // declare Y axis object
    private TextView zCoor; // declare Z axis object
    private TextView SampleRate;
    private TextView TvTimer;*/
    private TextView steps;
    private TextView TvAnalyzingTime;
    private TextView TvBPM;
    private Button ResetBtn;
    private Button PlayBtn;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    DatabaseAdapter adapter ;
    long SongId;

    //Step detection
    int totalSteps = 0;
    int tempStepCounter = 0; // temporary counter
    int c = 0; // c = array index
    boolean step = false; // are you making a step?
    //double[] samples = new double[3]; // store 3 accelero-data samples (for accuracy) in array
    //int samplecounter = 0; //sample rate

    //BPM calculation
    int SetIteration = 10; // amount of steps for BPM calc
    double TimeBetweenSteps[] = new double[SetIteration]; // store steps in array
    double BPM = 0; //beats per minute (gets calculated)

    //timer
    double Time = 0; //will be used to store millis timer into and make together with PreviousTime, the AnalyzingTime
    double PreviousTime = 0;
    double AnalyzingTime = 0;
    String text = "Steps Per Minute: ";

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_response);

/*      xCoor=(TextView)findViewById(R.id.xcoor); // create X axis object
        yCoor=(TextView)findViewById(R.id.ycoor); // create Y axis object
        zCoor=(TextView)findViewById(R.id.zcoor); // create Z axis object
        SampleRate = (TextView) findViewById(R.id.TVwSampleRate);
        TvTimer = (TextView) findViewById(R.id.TvTimer);*/
        steps = (TextView) findViewById(R.id.steps);
        TvAnalyzingTime = (TextView) findViewById(R.id.TvAnalyzingTime);
        TvBPM = (TextView) findViewById(R.id.TvBPM);
        adapter = new DatabaseAdapter(getApplicationContext());

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be  (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        PlayBtn =(Button) findViewById(R.id.btnPlaySong);
        ResetBtn = (Button) findViewById(R.id.btnReset); // performance-check: public void btnReset, double code?
        ResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempStepCounter = 0;
                //samplecounter = 0;
                AnalyzingTime = 0;
                text =  "Steps Per Minute: ";
                TvBPM.setText(text);
                steps.setText("Steps: " + Integer.toString(tempStepCounter));
            }
        });

        PlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double bpm1 = BPM -20;
                double bpm2 = BPM + 20;

                SongId=adapter.getSongID(bpm1,bpm2);
                int id = (int) SongId;
                musicSrv.setSong(id);
                musicSrv.playMatchSong();
            }
        });

        TvAnalyzingTime.setText("Analyzing Time: <N/A>");
        TvBPM.setText(text);
        //TvTimer.setText("Time: ");
    }
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list

            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }
    public void onAccuracyChanged(Sensor sensor,int accuracy){
    }

    public void btnReset(View view) {
        tempStepCounter = 0;
        //samplecounter = 0;
    }

    public void onSensorChanged(SensorEvent event){
        // check sensor type
        // assign directions
        //samplecounter++;

        double x=event.values[0];
        double y=event.values[1]; // acceleration speed
        double z=event.values[2];

        /*xCoor.setText("X: "+ x);
        yCoor.setText("Y: "+ y);
        zCoor.setText("Z: "+ z);*/

        if( y > 13) // prevent bounce (false) values by applying hysteresis level triggering: above 13 = from false to true, beneath 12 = from true to false.
        {
            // when median is above 13, it counts as a step. to prevent it will keep counting steps when the next samples
            // are also above 13 (same step), we only increment if the previous sample is below 12 ( step = false)

            if (!step)
            {
                totalSteps++;
                c = tempStepCounter % SetIteration; // c = an array index, SetIteration = variable amount of steps taken, used to analyze BPM
                tempStepCounter++; // amount of steps increased by 1
                Time = android.os.SystemClock.uptimeMillis(); // Time in millis from when your screen is lit.

                TimeBetweenSteps[c] = Time - PreviousTime;
                if (0 <= (Time - PreviousTime)/1000 && (Time - PreviousTime)/1000 <= 5) //We assume that in a 1s iteration (60BPM), no1 is running anymore.
                {
                    AnalyzingTime += TimeBetweenSteps[c]/1000; // Add all timestamps together
                }
                else {

                    tempStepCounter = 0; // put values on 0 to start new analysis
                    AnalyzingTime = 0;
                }

                if( c == SetIteration -1) // it will never hit the iteration value because c will be 0 then.  (c = tempStepCounter % SetIteration)
                {
                    TvAnalyzingTime.setText("Analyzing Time: " + AnalyzingTime + "s per "+ SetIteration +" steps");
                    BPM =  SetIteration / AnalyzingTime * 60; // amount of steps divided by the time to take those steps = step/second. multiply by 60 and you have steps/minute
                    text += ",\n "+ BPM;
                    TvBPM.setText("SPM: " + text);
                    AnalyzingTime = 0;
                }
                step = true; // put step on true for correct BPM data.
                PreviousTime = Time;
                //text += ", [" + c + "]" + TimeBetweenSteps[c];
                steps.setText("Total steps: "+totalSteps + ", steps until next analysis "+(SetIteration - c - 1));
                //TvTimer.setText(text);
            }
            //SampleRate.setText(Integer.toString(samplecounter));
        }
        else if( y < 12){
            step = false;
        }
    }
}