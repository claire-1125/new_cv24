package com.example.new_cv24;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    static final int PORT = 5555;  //임의로 정한 포트

    //TCP를 이용했다.
    ServerSocket serversocket;
    Socket socket;
    DataInputStream is;
    DataOutputStream os;

    //TextView text_msg; //클라이언트로부터 '받을' 메세지를 표시하는 TextView
    //EditText edit_msg; //클라이언트로 '전송할' 메세지를 작성하는 EditText

    String recvMsg = "";  //클라이언트로부터 받은 메시지를 저장
    String sendMsg;
    //boolean isConnected = true;  //딱히 flag tag는 아닌 듯...;;

    //Runnable rnb;

    public Server(){
        try {
            serversocket = new ServerSocket(PORT);  //서버소켓 생성
            //서버에 접속하는 클라이언트 소켓 얻어오기 (클라이언트가 접속하면 클라이언트 소켓 리턴)
            socket = serversocket.accept(); //서버는 클라이언트가 접속할 때까지 여기서 대기

            //클라이언트 접속 이후 데이터를 주고 받기 위한 통로구축
            is = new DataInputStream(socket.getInputStream()); //클라이언트로부터 메세지를 받기 위한 통로
            os = new DataOutputStream(socket.getOutputStream()); //클라이언트로 메세지를 보내기 위한 통로
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        //클라이언트가 접속을 끊을 때까지 무한반복하면서 클라이언트의 메세지 수신
        while(true) {
            try {
                //클라이언트로부터 메시지가 전송되면 이를 UTF 형식으로 읽어서 String으로 리턴
                recvMsg = is.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*//클라이언트로부터 읽어들인 recvMsg를 TextView에 출력
            //안드로이드는 오직 main Thread 만이 UI를 변경할 수 있기에
            //네트워크 작업을 하는 이 Thread에서는 TextView의 글씨를 직접 변경할 수 없음.
            //runOnUiThread()는 별도의 Thread가 main Thread에게 UI 작업을 요청하는 메소드임.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text_msg.setText(recvMsg);
                }
            });*/

        }
    }


    /*new Thread(new Runnable() {
        @Override
        //run method
    }).start(); //Thread 실행*/

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        text_msg = (TextView)findViewById(R.id.text_massage_from_client);
        edit_msg = (EditText)findViewById(R.id.edit_message_to_client);
    }*/


    //START, SEND 버튼 클릭 리스너
    /*public void mOnClick(View v){
        switch(v.getId()){
            case R.id.btn_start_server: //START : 채팅 서버 구축 및 클라이언트로부터 메세지 받기

                break;

            case R.id.btn_send_server: // SEND : 클라이언트로 메세지 전송하기
                if(os == null) return; //클라이언트와 연결되어 있지 않다면 전송불가
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //클라이언트로 보낼 메세지 EditText로 부터 얻어오기
                        sendMsg = edit_msg.getText().toString();
                        try {
                            os.writeUTF(sendMsg); //클라이언트로 UTF 방식 이용하여 메세지 보내기 (한글 전송가능)
                            os.flush();   //다음 메세지 전송을 위해 연결통로의 버퍼를 지워주기
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start(); //Thread 실행..

                break;
        }
    }*/
}
