package com.example.new_cv24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class HomePage extends AppCompatActivity {

    txtThread textTrd;
    String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        /*TextView header = findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "pop up setting",Toast.LENGTH_SHORT).show();

                //텍스트 데이터 수신 thread 생성 및 시작
                textTrd = new txtThread(9090);
                textTrd.start();
            }
        });*/

        Button database = findViewById(R.id.database);
        database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Board.class);
                startActivity(intent);
            }
        });
    }


    /*텍스트 데이터 UDP 통신 thread 클래스*/
    class txtThread extends Thread {
        DatagramSocket socket;

        public txtThread(int port){
            try {
                socket = new DatagramSocket(port);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            try {
                while (true) {

                    //수신용 패킷 생성
                    byte[] rBuf = new byte[1024];
                    DatagramPacket rPacket = new DatagramPacket(rBuf, rBuf.length);

                    //패킷 받음
                    socket.receive(rPacket);

                    //패킷을 보낸 상대방의 IP주소와 포트번호를 따로 저장
                    InetAddress ina = rPacket.getAddress();
                    int inp = rPacket.getPort();

                    //클라이언트에서 보낸 텍스트(즉, jsonStr) 저장
                    jsonStr = new String(rPacket.getData(),0,rPacket.getLength(),"UTF-8");

                    OpenPopUp(jsonStr);

                    //답장 전송
                    byte[] sBuf = ("텍스트 데이터를 정상적으로 받았습니다.").getBytes();
                    DatagramPacket sPacket = new DatagramPacket(sBuf, sBuf.length, ina, inp);
                    socket.send(sPacket);

                    //쓰레드를 인터럽트로 종료시키기 위해 sleep을 사용함
                    sleep(20);
                }
            } catch (InterruptedException e){
            }
            catch (Exception e){
            }
        }
    }

    private void OpenPopUp(CharSequence message){
        Intent intent = new Intent(this, alertDanger.class);
        intent.putExtra("violence case info", message);
        startActivity(intent);
    }
}