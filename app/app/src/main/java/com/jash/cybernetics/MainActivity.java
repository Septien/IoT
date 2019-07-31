package com.jash.cybernetics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

// For internet connection
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void btnOnClickPlts(View view) {
        Intent actSec = new Intent(this, plotsActivity.class);
        startActivity(actSec);
    }

    public void btnOnClickCtrls(View view) {
        Intent actSec = new Intent(this, ctrlsActivity.class);
        startActivity(actSec);
    }
}
