package com.jash.cybernetics;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;


public class ctrlsActivity extends AppCompatActivity implements android.widget.AdapterView.OnItemSelectedListener {
    private static final String[] sampling = new String[]{"0.5 min", "1 min", "1.5 min", "2 min", "2.5 min", "3 min", "3.5 min", "4 min", "4.5 min", "5 min"};
    private float samplingP;
    private int minuteL, hourL, dayL, monthL, yearL;
    private int minuteU, hourU, dayU, monthU, yearU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrls);

        // Get the widgets
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sampling);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        minuteL =  -1;
        hourL = -1;
        dayL = -1;
        monthL = -1;
        yearL = -1;
        minuteU = -1;
        hourU = -1;
        dayU = -1;
        monthU = -1;
        yearU = -1;
    }

    // Callback for reset button
    public void btnOnClickRst(View view) {
        if (MainActivity.q == null) {
            return;
        }
        try {
            MainActivity.q.put("SET");
            String cmd = "RS,1;LI,-1;UI,-1;SP,-1;SV,-1;TF,-1";
            MainActivity.q.put(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Callback for turn off led button
    public void btnOnClickLOff(View view) {
        if (MainActivity.q == null) {
            return;
        }
        try {
            MainActivity.q.put("SET");
            String cmd = "RS,-1;LI,-1;UI,-1;SP,-1;SV,-1;TF,1";
            MainActivity.q.put(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void btnOnClickSend(View view) {
        if (MainActivity.q == null) {
            return;
        }
        RadioButton radioButtonY = (RadioButton) findViewById(R.id.radio_yes);
        RadioButton radioButtonN = (RadioButton) findViewById(R.id.radio_no);
        Calendar calLow = Calendar.getInstance();
        Calendar calUp = Calendar.getInstance();
        calLow.set(yearL, monthL, dayL, hourL, minuteL);
        calUp.set(yearU, monthU, dayU, hourU, minuteU);
        long timeInMillsL = calLow.getTimeInMillis();
        long timeInMillsU = calUp.getTimeInMillis();
        if (timeInMillsL < 0)
            timeInMillsL = -1;
        if (timeInMillsU < 0)
            timeInMillsU = -1;
        int state = 0;
        if (radioButtonY.isChecked())
            state = 1;
        if (radioButtonN.isChecked())
            state = 0;
        try {
            MainActivity.q.put("SET");
            String cmd = "RS,-1;" + "LI," + timeInMillsL + ";UI," + timeInMillsU + ";SP," + samplingP + ";SV," + state + ";TF,-1";
            MainActivity.q.put(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTimePickerDialogL(View view) {
        TimePickerFragment newFragment = new TimePickerFragment(getApplicationContext());
        newFragment.show(getSupportFragmentManager(), "timePicker");
        this.minuteL = newFragment.getMinute();
        this.hourL = newFragment.getHour();
    }

    public void showDatePickerDialogL(View view) {
        DatePickerFragment newFragment = new DatePickerFragment(getApplicationContext());
        newFragment.show(getSupportFragmentManager(), "datePicker");
        this.dayL = newFragment.getDay();
        this.monthL = newFragment.getMonth();
        this.yearL = newFragment.getYear();
    }

    public void showTimePickerDialogU(View view) {
        TimePickerFragment newFragment = new TimePickerFragment(getApplicationContext());
        newFragment.show(getSupportFragmentManager(), "timePicker");
        this.minuteU = newFragment.getMinute();
        this.hourU = newFragment.getHour();
    }

    public void showDatePickerDialogU(View view) {
        DatePickerFragment newFragment = new DatePickerFragment(getApplicationContext());
        newFragment.show(getSupportFragmentManager(), "datePicker");
        this.dayU = newFragment.getDay();
        this.monthU = newFragment.getMonth();
        this.yearU = newFragment.getYear();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String period = parent.getItemAtPosition(position).toString();
        Toast alert = Toast.makeText(getApplicationContext(), period, Toast.LENGTH_SHORT);
        alert.show();
        String[] split = period.split(" ");
        samplingP = Float.parseFloat(split[0]) * 60.0f;
    }

    // Necessary for array adapter
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        samplingP = -1;
    }
}
