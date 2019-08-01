package com.jash.cybernetics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    public static BlockingDeque<String> q;
    private Thread clientThread = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        q = new LinkedBlockingDeque<String>();
        clientThread = new client("127.0.0.1", 8080, q);
        clientThread.run();
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
