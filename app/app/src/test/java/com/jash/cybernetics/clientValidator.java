package com.jash.cybernetics;

/* Specific libraries for testing */
import android.util.Log;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/* For working with the client */
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
* Class for testing the correct working of the client class.
* */
@RunWith(MockitoJUnitRunner.class)
public class clientValidator {
    private String dstAddress;
    private int dstPort;
    private BlockingQueue<String> cmdQ;
    private BlockingQueue<String> dataQ;
    private Thread clientThread = null;
    private ServerSocket server;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    @Before
    public void initMocks() {
        dstAddress = "127.0.0.1";
        dstPort = 8080;
        cmdQ = new LinkedBlockingQueue<String>(1000);
        dataQ = new LinkedBlockingQueue<String>(1000);

        // Init server
        try {
            server = new ServerSocket(dstPort);
            // Run client
            clientThread = new client(dstAddress, dstPort, cmdQ, dataQ);
            clientThread.start();
            socket = server.accept();
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testcmdSET() {
        String cmd;
        try {
            // Reset
            cmdQ.add("SET, ");
            cmdQ.put("RS,1");
            cmdQ.put("LI,-1");
            cmdQ.put("UI,-1");
            cmdQ.put("SP,-1");
            cmdQ.put("SV,-1");
            cmdQ.put("TF,-1");

            cmd = input.readLine();
            output.println("ack");
            assertEquals(cmd, "SET");
            cmd = input.readLine();
            output.println("ack");
            String[] cmdS = cmd.split(";");
            assertEquals(cmdS.length, 6);
            String[] cmdS2 = cmdS[0].split(",");
            assertEquals(cmdS2.length, 2);
            assertEquals(cmdS2[0], "RS");
            assertEquals(cmdS2[1], "1");
            for (int i = 1; i < cmdS.length; i++) {
                cmdS2 = cmdS[i].split(",");
                assertEquals(cmdS2.length, 2);
                assertEquals(cmdS2[1], "-1");
            }

            // Turn off led
            cmdQ.add("SET, ");
            cmdQ.put("RS,-1");
            cmdQ.put("LI,-1");
            cmdQ.put("UI,-1");
            cmdQ.put("SP,-1");
            cmdQ.put("SV,-1");
            cmdQ.put("TF,1");

            cmd = input.readLine();
            output.println("ack");
            assertEquals(cmd, "SET");
            cmd = input.readLine();
            output.println("ack");
            cmdS = cmd.split(";");
            assertEquals(cmdS.length, 6);
            for (int i = 0; i < cmdS.length - 1; i++) {
                cmdS2 = cmdS[i].split(",");
                assertEquals(cmdS2.length, 2);
                assertEquals(cmdS2[1], "-1");
            }
            cmdS2 = cmdS[5].split(",");
            assertEquals(cmdS2.length, 2);
            assertEquals(cmdS2[0], "TF");
            assertEquals(cmdS2[1], "1");

            // Other parameters
            cmdQ.add("SET, ");
            cmdQ.put("RS,-1");
            cmdQ.put("LI," + 100);
            cmdQ.put("UI," + 100);
            cmdQ.put("SP," + 100);
            cmdQ.put("SV," + 100);
            cmdQ.put("TF,-1");

            cmd = input.readLine();
            output.println("ack");
            assertEquals(cmd, "SET");
            cmd = input.readLine();
            output.println("ack");
            cmdS = cmd.split(";");
            assertEquals(cmdS.length, 6);
            cmdS2 = cmdS[0].split(",");
            assertEquals(cmdS2.length, 2);
            assertEquals(cmdS2[0], "RS");
            assertEquals(cmdS2[1], "-1");
            for (int i = 1; i < cmdS.length - 1; i++) {
                cmdS2 = cmdS[i].split(",");
                assertEquals(cmdS2.length, 2);
                assertEquals(cmdS2[1], "100");
            }
            cmdS2 = cmdS[5].split(",");
            assertEquals(cmdS2.length, 2);
            assertEquals(cmdS2[0], "TF");
            assertEquals(cmdS2[1], "-1");


        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testcmdGET() {
        String cmd;
        String[] cmdS, cmdS2;

        try {
            cmdQ.put("GET");
            output.println("DHT,23,23");
            output.println("DHT,24,24");
            output.println("DHT,25,25");
            output.println("DHT,26,26");
            output.println("HC,12/13/14,1");
            output.println("DONE");

            cmd = input.readLine();
            assertEquals(cmd, "GET");
            while (!dataQ.isEmpty()) {
                cmd = dataQ.take();
                cmdS = cmd.split(",");
                assertEquals(cmdS.length, 3);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testcmdEXIT() {
        try {
            cmdQ.put("EXIT");
            server.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
