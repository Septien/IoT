package com.jash.cybernetics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class ctrlsActivity extends AppCompatActivity implements android.widget.AdapterView.OnItemSelectedListener {
    private static final String[] sampling = new String[]{"0.5 min", "1 min", "1.5 min", "2 min", "2.5 min", "3 min", "3.5 min", "4 min", "4.5 min", "5 min"};
    private Button btnReset;
    private Button btnSend;
    private Button btnLedOff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrls);

        // Get the widgets
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        btnSend = (Button) findViewById(R.id.buttonSend);
        btnReset = (Button) findViewById(R.id.buttonRst);
        btnLedOff = (Button) findViewById(R.id.buttonLEDOFF);

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

    }

    // Callback for turn off led button
    public void btnOnClickLOff(View view) {

    }

    public void btnOnClickSend(View view) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        return;
    }

    // Necessary for array adapter
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }
}
