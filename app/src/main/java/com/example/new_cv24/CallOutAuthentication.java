package com.example.new_cv24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//alertDanger에서 전달받은 intent extra message를
//1. '출동' 버튼을 누른 사람
//1) 본인 화면에 표시 : 이 intent를 그대로 OnTheSpot에 전달
//2) 다른 사람들에게 전달 : extra message를 UDP로 전송
//2. 버튼 안 누른 사람 : 이 extra message(String 타입)을 UDP를 통해 받는다.

//30초 이상 버튼을 누르지 않을 경우 HomePage로 돌아가고
//'출동' 버튼을 누른 사람이 패킷 전송을 할 때까지 기다리기...?



//UDP에서 받을 때 : 보낸 사람이 만든 포트의 소캣 생성 > 받음
//UDP에서 보낼 때 : 대상 IP주소와 포트를 지정해서 보낸다.
//브로드캐스트에서 IP주소 지정? 공유기 IP주소?
//cmd의 ipconfig에서 기본 게이트웨이가 공유기 IP주소
//192.168.0.1


public class CallOutAuthentication extends AppCompatActivity {

    int PORT = 5555;  //해당 버튼을 누른 기기들이 브로드캐스팅을 하기 위해 설정한 포트
    txtThread textTrd;
    String data;
    CountDownTimer countDownTimer;  //버튼을 누르지 않는 시간을 측정하기 위해 필요

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_out_authentication);

        //잠금화면이어도 화면 뜨게 설정
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        //생각해보니 출동하지 않은 사람도 그냥 이 extra data를 가지고
        //AlreadySbOnTheSpot로 가도 된다.
        Intent intent = getIntent();
        data = intent.getStringExtra("violence case info");

        /*//버튼 클릭 여부(출동 여부.)와 관련없이 소캣은 생성해야 함.
        //(데이터를 보내든 받든 소캣은 있어야 하므로)
        textTrd = new txtThread(PORT);*/

        Button callOut = (Button) findViewById(R.id.goHome);
        callOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                /*//1 - 2) 다른 사람들에게 전달
                textTrd.start();  //소캣 생성 이후에 데이터 주고받는 부분*/

                //1 - 1) 본인 화면에 표시
                Intent intent = new Intent(getApplicationContext(), OnTheSpot.class);
                intent.putExtra("violence case info", data);
                startActivity(intent);
            }
        });

        /*Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int c_hour = calendar.get(Calendar.HOUR_OF_DAY);
        int c_min = calendar.get(Calendar.MINUTE);
        int c_sec = calendar.get(Calendar.SECOND);

        Calendar moment = new GregorianCalendar(year,month,day,c_hour,c_min,c_sec);   //호출 순간의 시간

        //30s = 30000ms (Because of 1s = 1000ms, 1m = 60s)
        //30초의 시간 내에서 1초마다 작동하는 타이머
        countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //호출한 순간의 시간과 현재 시간의 차를 구해서
                //그 차이가 30초 이상이면 HomePage로 넘어가도록 함
                getTime(moment);
            }

            @Override
            public void onFinish() {

            }
        };

        countDownTimer.start();  //실제로 onTick()을 호출한다.*/

        /*//패킷을 받았냐의 여부를 어떻게 확인하지....?
        //먼저 receive 해서 null이면 아무 동작 하지 않기..?
        //해당 코드 때문에 timer가 작동하지 않는다....
        textTrd.packetCheck();*/
    }

    /*텍스트 데이터 UDP 통신 thread 클래스*/
    class txtThread extends Thread {
        DatagramSocket socket;

        public txtThread(int port){
            try {
                socket = new DatagramSocket(port);
                socket.setBroadcast(true);  //1:n 통신을 할 때도 존재하므로
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        //데이터가 들어있는 패킷인가 확인
        public void packetCheck(){
            try {
                while (true) {
                    byte[] rBuf = new byte[1024];
                    DatagramPacket rPacket = new DatagramPacket(rBuf, rBuf.length);
                    socket.receive(rPacket);

                    if(rPacket != null){  //'출동' 버튼을 누른 사람이 데이터를 보낸 상황

                        //클라이언트에서 보낸 텍스트(즉, jsonStr) 저장
                        String jsonStr = new String(rPacket.getData(),0,rPacket.getLength(),"UTF-8");

                        Intent intent = new Intent(getApplicationContext(), AlreadySbOnTheSpot.class);
                        intent.putExtra("violence case info", jsonStr);
                        startActivity(intent);
                    }

                    //쓰레드를 인터럽트로 종료시키기 위해 sleep을 사용함
                    sleep(20);
                }
            } catch (Exception e){

            }

        }

        public void run(){
            try {
                while (true) {

                    /*//수신용 패킷 생성
                    byte[] rBuf = new byte[1024];
                    DatagramPacket rPacket = new DatagramPacket(rBuf, rBuf.length);

                    //패킷 받음
                    socket.receive(rPacket);

                    //패킷을 보낸 상대방의 IP주소와 포트번호를 따로 저장
                    InetAddress ina = rPacket.getAddress();
                    int inp = rPacket.getPort();

                    //클라이언트에서 보낸 텍스트(즉, jsonStr) 저장
                    jsonStr = new String(rPacket.getData(),0,rPacket.getLength(),"UTF-8");

                    OpenPopUp(jsonStr);*/

                    //이 데이터를 보내야 한다.
                    //String data = intent.getStringExtra("violence case info");

                    //위험상황 정보를 버튼을 누르지 않은 나머지 기기에게 전송
                    byte[] AP = "192.168.0.1".getBytes();
                    InetAddress ap = InetAddress.getByAddress(AP);
                    byte[] sBuf = data.getBytes();  //위험상황 정보 (즉, jsonStr)
                    DatagramPacket sPacket = new DatagramPacket(sBuf, sBuf.length,ap,PORT);
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


    /*1분 이상 '출동' 버튼을 누르지 않으면 HomePage로 이동*/
    private void getTime(Calendar moment){
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int c_hour = calendar.get(Calendar.HOUR_OF_DAY);
        int c_min = calendar.get(Calendar.MINUTE);
        int c_sec = calendar.get(Calendar.SECOND);

        //현재 시간과 해당 함수를 호출하는 순간의 시간을 비교한다.....
        Calendar current = new GregorianCalendar(year,month,day,c_hour,c_min,c_sec);   //현재 시간

        //초 단위의 차이 구하기
        long diffSec = (current.getTimeInMillis() - moment.getTimeInMillis()) / 1000;

        //만약 30초 이상이면 HomePage로 넘어가기
        if(diffSec >= 30){
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            startActivity(intent);
        }
    }
}