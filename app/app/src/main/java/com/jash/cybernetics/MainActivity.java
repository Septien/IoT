package com.jash.cybernetics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    public static BlockingQueue<String> q = null;
    public static BlockingQueue<String> dataQ = null;
    private Thread clientThread = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        q = new LinkedBlockingQueue<String>(1000);
        dataQ = new LinkedBlockingQueue<String>(1000);
        clientThread = new client("192.168.43.215", 8080, q, dataQ);//"192.168.1.71"
        clientThread.start();
    }

    public void btnOnClickPlts(View view) {
        Intent actSec = new Intent(this, plotsActivity.class);
        startActivity(actSec);
    }

    public void btnOnClickCtrls(View view) {
        Intent actSec = new Intent(this, ctrlsActivity.class);
        startActivity(actSec);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            q.put("EXIT");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                clientThread.join();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
