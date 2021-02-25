package com.example.new_cv24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Waiting extends AppCompatActivity {

    ServerSocket serversocket;
    static final int PORT = 5555;  //임의로 정한 포트
    Socket socket;
    DataInputStream is;
    DataOutputStream os;
    String recvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting);

        Intent get = getIntent();
        String data = get.getStringExtra("violence case info");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serversocket = new ServerSocket(PORT);  //서버소켓 생성
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    //서버에 접속하는 클라이언트 소켓 얻어오기 (클라이언트가 접속하면 클라이언트 소켓 리턴)
                    socket = serversocket.accept(); //서버는 클라이언트가 접속할 때까지 여기서 대기

                    //클라이언트 접속 이후 데이터를 주고 받기 위한 통로구축
                    is = new DataInputStream(socket.getInputStream()); //클라이언트로부터 메세지를 받기 위한 통로
                    os = new DataOutputStream(socket.getOutputStream()); //클라이언트로 메세지를 보내기 위한 통로

                } catch (IOException e) {
                    e.printStackTrace();
                }


                //클라이언트가 접속을 끊을 때까지 무한반복하면서 클라이언트의 메세지 수신
                while(true) {
                    try {
                        //클라이언트로부터 메시지가 전송되면 이를 UTF 형식으로 읽어서 String으로 리턴
                        recvMsg = is.readUTF();
                        if (recvMsg != null) {
                            Intent intent = new Intent(getApplicationContext(), AlreadySbOnTheSpot.class);
                            intent.putExtra("violence case info", data);
                            startActivity(intent);
                        } else {
                            Waiting.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"아직 못 받았습니다.",Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }//while
            }//run method
        }).start(); //Thread 실행

    }

}