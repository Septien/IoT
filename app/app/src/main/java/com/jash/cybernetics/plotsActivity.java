package com.jash.cybernetics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class plotsActivity extends AppCompatActivity {
    private LineGraphSeries<DataPoint> tempSeries;
    private LineGraphSeries<DataPoint> humiSeries;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plots);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Create the graphs
        GraphView graphT = (GraphView) findViewById(R.id.graphT);
        GraphView graphH = (GraphView) findViewById(R.id.graphH);

        tempSeries = new LineGraphSeries<DataPoint>();
        humiSeries = new LineGraphSeries<DataPoint>();

        // Style series
        tempSeries.setTitle("Temperatura");
        tempSeries.setColor(Color.GREEN);
        humiSeries.setTitle("Humedad");
        humiSeries.setColor(Color.BLUE);
    }

    @Override
    protected  void onResume() {
        super.onResume();
        // simulate real time update with threads
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Add 1000 entries
                for (int i = 0; i < 1000; i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addData();
                        }
                    });

                    try {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();       // Return to previous view
        }
        return super.onOptionsItemSelected(item);
    }

    private void addData() {
        if (MainActivity.dataQ == null)
            return;
        String cmd = MainActivity.dataQ.poll();
        if (cmd == null)
            return;
        String[] cmdS = cmd.split(",");
        if (cmdS[0].equals("DHT")) {
            int temp = Integer.parseInt(cmdS[1]);
            int humi = Integer.parseInt(cmdS[2]);
            tempSeries.appendData(new DataPoint(lastX, temp), true, 100);
            humiSeries.appendData(new DataPoint(lastX++, humi), true, 100);
        }
        else {
            Toast alert = Toast.makeText(getApplicationContext(), "Se detectó una presencia", Toast.LENGTH_SHORT);
            alert.show();
        }
    }
}
