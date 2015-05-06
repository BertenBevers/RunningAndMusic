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

public class StepResponse extends Activity implements SensorEventListener {
    private SensorManager sensorManager;

    private TextView xCoor; // declare X axis object
    private TextView yCoor; // declare Y axis object
    private TextView zCoor; // declare Z axis object
    private TextView steps;
    private TextView SampleRate;
    private TextView TvTimer;
    private TextView TvAnalyzingTime;
    private TextView TvBPM;
    private Button ResetBtn;

    //Step detection
    int StepCounter = 0; //
    int c = 0; // c = array index
    int samplecounter = 0; //sample rate
    boolean step = false; // are you making a step?
    double[] samples = new double[3]; // store 3 accelero-data samples (for accuracy) in array

    //BPM calculation
    int SetIteration = 10; // amount of steps for BPM calc
    double TimeBetweenSteps[] = new double[SetIteration]; // store steps in array
    double BPM = 0; //beats per minute (gets calculated)

    //timer
    double Time = 0; //will be used to store millis timer into and make together with PreviousTime, the AnalyzingTime
    double PreviousTime = 0;
    double AnalyzingTime = 0;
    String text = "Iteration in seconds: ";

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_response);

        xCoor=(TextView)findViewById(R.id.xcoor); // create X axis object
        yCoor=(TextView)findViewById(R.id.ycoor); // create Y axis object
        zCoor=(TextView)findViewById(R.id.zcoor); // create Z axis object
        SampleRate = (TextView) findViewById(R.id.TVwSampleRate);
        steps = (TextView) findViewById(R.id.steps);
        TvTimer = (TextView) findViewById(R.id.TvTimer);
        TvAnalyzingTime = (TextView) findViewById(R.id.TvAnalyzingTime);
        TvBPM = (TextView) findViewById(R.id.TvBPM);

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be  (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        ResetBtn = (Button) findViewById(R.id.btnReset); // performance-check: public void btnReset, double code?
        ResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepCounter = 0;
                samplecounter = 0;
                AnalyzingTime = 0;
                steps.setText("Stappen: " + Integer.toString(StepCounter));
            }
        });
        TvAnalyzingTime.setText("Analyzing Time: ");
        TvBPM.setText("BPM: ");
        TvTimer.setText("Time: ");
    }

    public void onAccuracyChanged(Sensor sensor,int accuracy){
    }

    public void btnReset(View view) {
        StepCounter = 0;
        samplecounter = 0;
    }

    public void onSensorChanged(SensorEvent event){
        // check sensor type
        // assign directions
        samplecounter++;

        double x=event.values[0];
        double y=event.values[1]; // acceleration speed
        double z=event.values[2];

        xCoor.setText("X: "+ x);
        yCoor.setText("Y: "+ y);
        zCoor.setText("Z: "+ z);
/*

        for (i = 0; i < samples.length; i++ ) {
            samples[i] = y;
        }
        if (i == 2) {
            Arrays.sort(samples);
            median =  samples[samples.length/2];
        }
*/
        if( y > 13) // prevent bounce (false) values by applying hysteresis level triggering: above 13 = from false to true, beneath 12 = from true to false.
        {
            // when median is above 13, it counts as a step. to prevent it will keep counting steps when the next samples
            // are also above 13 (same step), we only increment if the previous sample is below 12 ( step = false)

            if (!step)
            {
                c = StepCounter % SetIteration; // c = an array index, SetIteration = variable amount of steps taken, used to analyze BPM
                StepCounter++; // amount of steps increased by 1
                Time = android.os.SystemClock.uptimeMillis(); // Time in millis from when your screen is lit.

                TimeBetweenSteps[c] = Time - PreviousTime;
                if (0 <= (Time - PreviousTime)/1000 && (Time - PreviousTime)/1000 <= 5) //We assume that in a 1s iteration (60BPM), no1 is running anymore.
                {
                    AnalyzingTime += TimeBetweenSteps[c]/1000; // Add all timestamps together

                }
                else {
                    StepCounter = 0; // put values on 0 to start new analysis
                    AnalyzingTime = 0;
                }

                if( c == SetIteration -1) // it will never hit the iteration value because c will be 0 then.  (c = StepCounter % SetIteration)
                {
                    TvAnalyzingTime.setText("Analyzing Time: " + AnalyzingTime + "s per "+ SetIteration +" steps");
                    BPM =  SetIteration / AnalyzingTime * 60; // amount of steps divided by the time to take those steps = step/second. multiply by 60 and you have steps/minute
                    TvBPM.setText("BPM: " + BPM);
                    AnalyzingTime = 0;
                }
                step = true; // put step on true for correct BPM data.
                PreviousTime = Time;

                xCoor.setText("X: "+ x);
                yCoor.setText("Y: "+ y);
                zCoor.setText("Z: "+ z);
                text += ", [" + c + "]" + TimeBetweenSteps[c];
                TvTimer.setText(text);
                steps.setText("Stappen: "+Integer.toString(StepCounter));
            }
            SampleRate.setText(Integer.toString(samplecounter));
        }
        else if( y < 12){
            step = false;
        }
    }
}

