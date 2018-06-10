package com.lifull.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.lifull.myapplication.BlueTooth;


public class MainActivity2 extends AppCompatActivity {
    static  final int REQUEST_ENABLE_BT = 10;
    public static final int MSG_VOICE_RECO_END = 1;
    public static final int MSG_VOICE_RECO_RESTART = 0;


    AudioManager audio;


    Thread mWorkerThread = null;
    byte[]readBuffer;
    int readBufferPosition;

    EditText edName;
    String name="이름";
    boolean nCount=false;
    String resultText;
    Button bt1,bt2;
    Button blueTooth,pt1,pt2,pt3;
    boolean started = false;
    boolean speech = false;

    ArrayAdapter<CharSequence> adspin;

    Spinner ptB1,ptB2,ptB3;

    long now ;
    Date date ;
    SimpleDateFormat sdf;
    String getTime ;

    BlueTooth mBlueTooth;
    Intent i;
    SpeechRecognizer mRecognizer;


    InputMethodManager imm;//키보드내리기
    LinearLayout lilayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //뷰페이저
        AutoScrollViewPager viewPager = (AutoScrollViewPager) findViewById(R.id.view_pager);
        ImageAdapter imgadapter = new ImageAdapter(this);
        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(imgadapter);
        viewPager.setAdapter(wrappedAdapter);
        viewPager.startAutoScroll();
        //뷰페이저
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 내리기
        lilayout = (LinearLayout)findViewById(R.id.layout1);//메인 레이아웃
        lilayout.setOnClickListener(myClickListener);

        mBlueTooth = new BlueTooth(this);

        adspin = ArrayAdapter.createFromResource(this,R.array.pattern,R.layout.support_simple_spinner_dropdown_item);
        edName = (EditText) findViewById(R.id.editText);
        bt1 = (Button)findViewById(R.id.bt1);
        bt2 = (Button)findViewById(R.id.bt2) ;
        blueTooth = (Button)findViewById(R.id.blueTooth) ;
        pt1 = (Button)findViewById(R.id.pt1);
        pt2 = (Button)findViewById(R.id.pt2);
        pt3 = (Button)findViewById(R.id.pt3);
        ptB1 = (Spinner)findViewById(R.id.ptB1);
        ptB2 = (Spinner)findViewById(R.id.ptB2);
        ptB3 = (Spinner)findViewById(R.id.ptB3);
        ptB1.setAdapter(adspin);
        ptB2.setAdapter(adspin);
        ptB3.setAdapter(adspin);
        now = System.currentTimeMillis();
        date = new Date(now);
        sdf = new SimpleDateFormat("HHmmss");
        getTime = sdf.format(date);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        View.OnClickListener listener3 = new View.OnClickListener()
        {
            public void onClick(View view)
            {
                checkBluetooth();
            }
        };
        blueTooth.setOnClickListener(listener3);
        View.OnClickListener listener1 = new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if (edName.getText().toString().length() == 0)
                {
                    Toast.makeText(MainActivity2.this,"텍스트 입력",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    name = edName.getText().toString();
                    InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(edName.getWindowToken(), 0);
                }
            }
        };
        bt2.setOnClickListener(listener1);
        View.OnClickListener listener2 = new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if(speech){
                    speech = false;
                    bt1.setText("음성인식");
                    mRecognizer.stopListening();
                    InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//키보드내리기
                    mInputMethodManager.hideSoftInputFromWindow(edName.getWindowToken(), 0);//키보드내리기
                }else{
                    speech = true;
                    bt1.setText("정지");
                    mRecognizer.startListening(i);

                    mBlueTooth.sendData(getTime);
                    audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_VIBRATE);
                    InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//키보드내리기
                    mInputMethodManager.hideSoftInputFromWindow(edName.getWindowToken(), 0);//키보드내리기

                }
            }
        };
        View.OnClickListener ptListener = new View.OnClickListener()
        {
            public void onClick(View view)
            {
                String value = ptB1.getSelectedItem().toString();
                if (value.equals("1"))
                {
                    mBlueTooth.sendData("b");

                }
                else if (value.equals("2"))
                {
                    mBlueTooth.sendData("c");

                }
                else if (value.equals("3"))
                {
                    mBlueTooth.sendData("d");

                }
            }
        };
        View.OnClickListener ptListener2 = new View.OnClickListener()
        {
            public void onClick(View view)
            {
                String value = ptB2.getSelectedItem().toString();
                if (value.equals("1"))
                {
                    mBlueTooth.sendData("e");
                }
                else if (value.equals("2"))
                {
                    mBlueTooth.sendData("f");
                }
                else if (value.equals("3"))
                {
                    mBlueTooth.sendData("g");
                }
            }
        };
        View.OnClickListener ptListener3 = new View.OnClickListener()
        {
            public void onClick(View view)
            {
                String value = ptB3.getSelectedItem().toString();
                if (value.equals("1"))
                {
                    mBlueTooth.sendData("h");
                }
                else if (value.equals("2"))
                {
                    mBlueTooth.sendData("i");
                }
                else if (value.equals("3"))
                {
                    mBlueTooth.sendData("j");
                }
            }
        };
        pt1.setOnClickListener(ptListener);
        pt2.setOnClickListener(ptListener2);
        pt3.setOnClickListener(ptListener3);
        bt1.setOnClickListener(listener2);
        //SpeechRecognizer의 초기화
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        //onPartialResults을 받기위한 초기값
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity2.this, "권한 허가", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity2.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("음성인식을하기 위해서는 접근 권한이 필요해요")
                .setDeniedMessage("거부하셨습니다...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.RECORD_AUDIO,Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN)

                .check();



    }





    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onRmsChanged(float rmsdB) {

        }
        @Override
        public void onResults(Bundle results) {


        }
        @Override
        public void onReadyForSpeech(Bundle params) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onPartialResults(Bundle partialResults) {

            receiveResults(partialResults);
        }
        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onError(int error) {
            mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END);
        }
        @Override public void onEndOfSpeech() {
            if (speech == true)
                mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_RESTART);
            else if(speech == false)
                mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END);
            nCount = false;
            started = false;
        }
        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub
        } @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub
        }
    };

    private Handler mHdrVoiceRecoState = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_VOICE_RECO_END	: if (speech == true)
                    mRecognizer.startListening(i);
                else if(speech   == false)
                    mRecognizer.stopListening();; break;

                case MSG_VOICE_RECO_RESTART	:  mRecognizer.startListening(i);	break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    private void receiveResults(Bundle results) {

        if ((results != null) ) {
            ArrayList<String> heard = results.getStringArrayList(mRecognizer.RESULTS_RECOGNITION);
            resultText = heard.get(0);
            if (!nCount){
                nCount = true;
                if (resultText.indexOf(name)!= -1)
                {
                    mBlueTooth.sendData("a");
                    Toast.makeText(MainActivity2.this,"인식성공",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }







    // onDestroy() : 어플이 종료될때 호출 되는 함수.
    //               블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
    @Override
    protected void onDestroy() {
        try{
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mBlueTooth.mInputStream.close();
            mBlueTooth.mSocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }

    void checkBluetooth() {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
         이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
         */
        mBlueTooth.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBlueTooth.mBluetoothAdapter == null ) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();

        }
        else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if(!mBlueTooth.mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)
                /**
                 startActivityForResult 함수 호출후 다이얼로그가 나타남
                 "예" 를 선택하면 시스템의 블루투스 장치를 활성화 시키고
                 "아니오" 를 선택하면 비활성화 상태를 유지 한다.
                 선택 결과는 onActivityResult 콜백 함수에서 확인할 수 있다.
                 */
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else // 블루투스 지원하며 활성 상태인 경우.
                mBlueTooth.selectDevice();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    mBlueTooth.selectDevice();
                }
                else if(resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                    Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(edName.getWindowToken(), 0);
    }

    View.OnClickListener myClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideKeyboard();
            switch (v.getId())
            {
                case R.id.layout1 :
                    break;
            }
        }
    };

}







