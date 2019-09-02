package com.jash.cybernetics;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class client extends Thread {
    private String dstAddress;
    private int dstPort;
    private BlockingQueue<String> cmdQ;
    private BlockingQueue<String> dataQ;

    client(String addr, int port, BlockingQueue<String> q, BlockingQueue<String> dQ) {
        dstAddress = addr;
        dstPort = port;
        cmdQ = q;
        dataQ = dQ;
        System.out.println("Create");
    }

    @Override
    public void run() {
        Socket socket = null;
        String cmd;//= new String[3];
        String[] cmdS;
        System.out.println("Connecting");
        try {
            socket = new Socket(dstAddress, dstPort);
            PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected");
            boolean endProc = false;
            while (!endProc) {
                cmd = cmdQ.poll();
                if (cmd == null) {
                    continue;
                }
                System.out.println(cmd);
                cmdS = cmd.split(",");
                // Leave
                if (cmdS[0].equals("EXIT"))
                    endProc = true;

                // Set new parameters to device
                else if (cmdS[0].equals("SET")) {
                    ArrayList<String> list = new ArrayList<String>();
                    String data = "";
                    data = cmdQ.poll();
                    /*cmdQ.drainTo(list, 7);
                    Iterator<String> it = list.iterator();
                    // Get parameters
                    while (it.hasNext()) {
                        cmd = it.next();
                        data = data + ";" + cmd;
                    }*/
                    System.out.println(data);
                    // Send to device
                    output.println("SET");
                    output.flush();
                    //cmd = input.readLine();
                    output.println(data);
                    output.flush();
                    System.out.println("Sent");
                }

                // Get data from device
                else if (cmdS[0].equals("GET")) {
                    System.out.println("GET");
                    output.println("GET");
                    boolean getData = true;
                    String msg = "";
                    int charsRead = 0;
                    char[] buffer = new char[1024];
                    while (getData) {
                        charsRead = input.read(buffer);
                        if (charsRead == -1) {
                            continue;
                        }
                        msg = new String(buffer).substring(0, charsRead);
                        if (msg.equals("DONE")) {
                            getData = false;
                            continue;
                        }
                        String[] msgC = msg.split(" ");
                        for (String m : msgC) {
                            // Send data to main process
                            System.out.println(m);
                            dataQ.put(m);
                        }
                    }
                }
            }
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
