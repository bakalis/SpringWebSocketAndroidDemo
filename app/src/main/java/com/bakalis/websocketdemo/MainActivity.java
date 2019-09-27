package com.bakalis.websocketdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import nbouma.com.wstompclient.implementation.StompClient;
import nbouma.com.wstompclient.model.Frame;


public class MainActivity extends AppCompatActivity {
    //The URL where the Spring WebSocket Server is located.
    private static final String url = "ws://192.168.1.4:8080/content/websocket";
    private StompClient stompClient;
    EditText nameView;
    EditText headerView;
    Button sendBtn;
    Button rcBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("WebSocket Application Demo");
        setContentView(R.layout.activity_main);
        //Get UI Components References
        sendBtn = (Button) findViewById(R.id.sendBtn);
        rcBtn = (Button) findViewById(R.id.rcBtn);
        nameView = (EditText) findViewById(R.id.nameView);
        headerView = (EditText) findViewById(R.id.headerView);
        sendBtn.setEnabled(false);

        //Connect to the WebSocket
        connect();

        //Setting the Listeners for the Send and Reconnect Buttons.
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checking if the Data the user Entered exist or are blank or whitespaces
                if (!(nameView.getText().toString().trim().length() == 0)
                        && !(nameView.getText() == null)
                        && !(headerView.getText().toString().trim().length() == 0)
                        && !(headerView.getText() == null)) {
                    stompClient.sendMessage("/app/hello", "{\"name\": \"" + nameView.getText() + "\", \"header\": \"" + headerView.getText() + "\"}");
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Incorrect Input!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        rcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
    }
    //Initializes a connection to the WebSocket and handles UI appropriately
    private void connect(){
        stompClient = new StompClient(url) {
            @Override
            protected void onStompError(final String errorMessage) {
                Log.d("TAG", "error : " + errorMessage);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            protected void onConnection(boolean connected) {
                Log.d("TAG", "connected : " + String.valueOf(connected));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendBtn.setEnabled(true);
                        rcBtn.setEnabled(false);
                    }
                });



            }

            @Override
            protected void onDisconnection(String reason) {
                Log.d("TAG", "disconnected : " + reason);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendBtn.setEnabled(false);
                        rcBtn.setEnabled(true);
                    }
                });
            }

            @Override
            protected void onStompMessage(final Frame frame) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), frame.getBody(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }


    //Close the WebSocket Connection when the Activity is destroyed.
    @Override
    protected void onDestroy(){
        super.onDestroy();
        stompClient.disconnect();
    }


}
