package com.example.new_cv24;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    private static final int PORT = 5555; //서버에서 설정한 PORT
    String ip = "192.168.0.14"; //서버의 IP주소

    Socket socket;  //클라이언트의 소켓
    DataInputStream is;
    DataOutputStream os;

    /*TextView text_msg;  //서버로부터 받은 메세지를 보여주는 TextView
    EditText edit_msg;  //서버로 전송할 메세지를 작성하는 EditText
    EditText edit_ip;   //서버의 IP를 작성할 수 있는 EditText*/

    String recvMsg = "", sendMsg;
    //boolean isConnected = true;

    public Client(String msg){
        while(true){  //서버소캣이 생성되기를 기다려야 하므로
            try {
                sendMsg = msg;
                //서버와 연결하는 소켓 생성
                socket = new Socket(InetAddress.getByName(ip), PORT);

                //소켓 연결 성공 후 서버와 메세지를 주고받을 통로 구축
                is = new DataInputStream(socket.getInputStream());
                os = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run(){
        try {
            os.writeUTF(sendMsg);  //서버로 UTF 이용하여 JSON 데이터 보내기
            os.flush();        //다음 메세지 전송을 위해 연결통로의 버퍼 지워주기
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        text_msg = (TextView)findViewById(R.id.text_massage_from_server);
        edit_msg = (EditText)findViewById(R.id.edit_message_to_server);
        edit_ip = (EditText)findViewById(R.id.edit_addressofserver);
        edit_ip.setText(ip);  //기본적으로 ip주소를 하드코딩한다.
    }*/


    //'서버 연결','SEND' 버튼 클릭 리스너
    /*public void mOnClick(View v){
        switch(v.getId()){
            case R.id.btn_connectserver:  //'서버 접속' : 서버에 접속하고 서버로 부터 메세지 수신하기
                new Thread(new Runnable() {
                    @Override
                    public void run() {



                        //서버와 접속이 끊길 때까지 무한반복하면서 서버의 메세지 수신
                        while(true){
                            try {
                                recvMsg = is.readUTF(); //서버에서 보낸 메시지를 UTF로 읽어서 String으로 리턴

                                //서버로부터 읽어들인 recvMsg를 TextView에 출력
                                //안드로이드는 오직 main Thread 만이 UI를 변경할 수 있기에
                                //네트워크 작업을 하는 이 Thread에서는 TextView의 글씨를 직접 변경할 수 없음.
                                //runOnUiThread()는 별도의 Thread가 main Thread에게 UI 작업을 요청하는 메소드임.

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        text_msg.setText(recvMsg);
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }//while
                    }//run method...
                }).start();//Thread 실행..

                break;

            case R.id.btn_send_client: //SEND : 서버로 메세지 전송하기
                if(os == null) return;   //서버와 연결되어 있지 않다면 전송불가

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }//run method..
                }).start(); //Thread 실행..

                break;
        }
    }*/

}
