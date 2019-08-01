package com.jash.cybernetics;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingDeque;

public class client extends Thread {
    String dstAddress;
    int dstPort;
    BlockingDeque<String> dataQ;
    String response;

    client(String addr, int port, BlockingDeque<String> q) {
        dstAddress = addr;
        dstPort = port;
        dataQ = q;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(dstAddress, dstPort);
            PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // input.readLine()
            // output.println()
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
