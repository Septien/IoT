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
    }

    @Override
    public void run() {
        Socket socket = null;
        String cmd;//= new String[3];
        String[] cmdS;
        try {
            socket = new Socket(dstAddress, dstPort);
            PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // input.readLine()
            // output.println()
            boolean endProc = false;
            int state = 0;
            while (!endProc) {
                cmd = cmdQ.poll();
                if (cmd == null) {
                    continue;
                }
                cmdS = cmd.split(",");
                // Leave
                if (cmdS[0].equals("EXIT"))
                    endProc = true;

                // Set new parameters to device
                else if (cmdS[0].equals("SET")) {
                    ArrayList<String> list = new ArrayList<String>();
                    String data = "";
                    cmdQ.drainTo(list, 7);
                    Iterator<String> it = list.iterator();
                    // Get parameters
                    while (it.hasNext()) {
                        cmd = it.next();
                        data = data + ";" + cmd;
                    }
                    // Send to device
                    output.println("SET");
                    cmd = input.readLine();
                    output.println(data);
                    cmd = input.readLine();
                }

                // Get data from device
                else if (cmdS[0].equals("GET")) {
                    output.println("GET");
                    boolean getData = true;
                    PrintWriter outT, outH;
                    while (getData) {
                        cmd = input.readLine();
                        if (cmd.equals("DONE")) {
                            getData = false;
                            continue;
                        }
                        // Send data to main process
                        dataQ.put(cmd);
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
