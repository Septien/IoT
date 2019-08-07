package com.jash.cybernetics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;


public class ctrlsActivity extends AppCompatActivity implements android.widget.AdapterView.OnItemSelectedListener {
    private static final String[] sampling = new String[]{"0.5 min", "1 min", "1.5 min", "2 min", "2.5 min", "3 min", "3.5 min", "4 min", "4.5 min", "5 min"};
    private int samplingP;
    private int minuteL, hourL, dayL, monthL, yearL;
    private int minuteU, hourU, dayU, monthU, yearU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrls);

        // Get the widgets
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sampling);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    // Callback for reset button
    public void btnOnClickRst(View view) {
        if (MainActivity.q == null) {
            return;
        }
        try {
            MainActivity.q.put("SET");
            MainActivity.q.put("RS,1");
            MainActivity.q.put("LI,-1");
            MainActivity.q.put("UI,-1");
            MainActivity.q.put("SP,-1");
            MainActivity.q.put("SV,-1");
            MainActivity.q.put("TF,-1");

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
            MainActivity.q.put("RS,-1");
            MainActivity.q.put("LI,-1");
            MainActivity.q.put("UI,-1");
            MainActivity.q.put("SP,-1");
            MainActivity.q.put("SV,-1");
            MainActivity.q.put("TF,1");
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
        int state = 0;
        if (radioButtonY.isChecked())
            state = 1;
        if (radioButtonN.isChecked())
            state = 0;
        try {
            MainActivity.q.put("SET");
            MainActivity.q.put("RS,-1");
            MainActivity.q.put("LI," + timeInMillsL);
            MainActivity.q.put("UI," + timeInMillsU);
            MainActivity.q.put("SP," + samplingP);
            MainActivity.q.put("SV," + state);
            MainActivity.q.put("TF, -1");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void showTimePickerDialogL(View view) {
        TimePickerFragment newFragment = new TimePickerFragment(getApplicationContext());
        newFragment.show(getSupportFragmentManager(), "timePicker");
        this.minuteL = newFragment.getMinute();
        this.hourL = newFragment.getHour();
    }

    public  void showDatePickerDialogL(View view) {
        DatePickerFragment newFragment = new DatePickerFragment(getApplicationContext());
        newFragment.show(getSupportFragmentManager(), "datePicker");
        this.dayL = newFragment.getDay();
        this.monthL = newFragment.getMonth();
        this.yearL = newFragment.getYear();
    }

    public  void showTimePickerDialogU(View view) {
        TimePickerFragment newFragment = new TimePickerFragment(getApplicationContext());
        newFragment.show(getSupportFragmentManager(), "timePicker");
        this.minuteU = newFragment.getMinute();
        this.hourU = newFragment.getHour();
    }

    public  void showDatePickerDialogU(View view) {
        DatePickerFragment newFragment = new DatePickerFragment(getApplicationContext());
        newFragment.show(getSupportFragmentManager(), "datePicker");
        this.dayU = newFragment.getDay();
        this.monthU = newFragment.getMonth();
        this.yearU = newFragment.getYear();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String period = parent.getItemAtPosition(position).toString();
        samplingP = Integer.parseInt(period);
    }

    // Necessary for array adapter
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        samplingP = -1;
    }
}

// Create a time picker
class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private int hour;
    private int minute;
    private Context context;
    TimePickerFragment(Context context) {
        this.context = context;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minuteN) {
        Calendar c = Calendar.getInstance();
        if (hourOfDay < c.get(Calendar.HOUR_OF_DAY) || minuteN < c.get(Calendar.MINUTE)) {
            Toast alert = Toast.makeText(context, "Seleccione una hora igual o posterior a la actual", Toast.LENGTH_SHORT);
            alert.show();
            return;
        }
        minute = minuteN;
        hour = hourOfDay;
    }


    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }
}

// Create a date picker
class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private int year;
    private int month;
    private int day;
    private Context context;

    DatePickerFragment(Context context) {
        this.context = context;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        if (year < c.get(Calendar.YEAR) || month < Calendar.MONTH || day < Calendar.DAY_OF_MONTH)
        {
            Toast alert = Toast.makeText(context, "Seleccione una fecha igual o posterior a la actual", Toast.LENGTH_SHORT);
            alert.show();
            return;
        }
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
