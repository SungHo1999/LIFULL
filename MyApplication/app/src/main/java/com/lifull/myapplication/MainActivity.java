package com.lifull.myapplication;
import java.io.InputStream;
import java.io.OutputStream;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Bundle;
import android.util.Log;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static  final int REQUEST_ENABLE_BT = 10;
    public static final int MSG_VOICE_RECO_END = 1;
    public static final int MSG_VOICE_RECO_RESTART = 0;
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;

    BluetoothDevice mRemoteDevie;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream;
    InputStream mInputStream;
    String mStrDelimiter = "\n";
    char mCharDelimiter = '\n';

    Thread mWorkerThread = null;
    byte[]readBuffer;
    int readBufferPosition;

    EditText edName;
    String name="이름";
    boolean nCount=false;
    String resultText;
    Button bt1,bt2;
    boolean started = false;
    boolean speech = false;
    boolean timec = false;
    TextView tv;
    TextView tv1;

    long now ;
    Date date ;
    SimpleDateFormat sdf;
    String getTime ;

    Intent i;
    Vibrator vibrator;
    SpeechRecognizer mRecognizer;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        edName = (EditText) findViewById(R.id.editText);
        bt1 = (Button)findViewById(R.id.bt1);
        bt2 = (Button)findViewById(R.id.bt2) ;
         now = System.currentTimeMillis();
       date = new Date(now);
         sdf = new SimpleDateFormat("HHmmss");
        getTime = sdf.format(date);


        View.OnClickListener listener1 = new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if (edName.getText().toString().length() == 0)
                {
                    Toast.makeText(MainActivity.this,"텍스트 입력",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    name = edName.getText().toString();
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
                    bt1.setText("Start");
                    mRecognizer.stopListening();
                }else{
                    speech = true;
                    bt1.setText("Stop");
                    mRecognizer.startListening(i);
                    if (!timec) {
                        timec = true;
                        sendData(getTime);
                        Toast.makeText(MainActivity.this,getTime,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        bt1.setOnClickListener(listener2);
        //SpeechRecognizer의 초기화
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        //onPartialResults을 받기위한 초기값
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        mContext = getApplicationContext();

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("음성인식을하기 위해서는 접근 권한이 필요해요")
                .setDeniedMessage("거부하셨습니다...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.RECORD_AUDIO,Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN)

                .check();

        checkBluetooth();

    }

    BluetoothDevice getDeviceFromBondedList(String name){
        BluetoothDevice selectedDevice =null;

        for (BluetoothDevice device : mDevices){
            if (name.equals((device.getName()))){
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }
    void sendData(String msg){
        msg += mStrDelimiter;
        try {
            mOutputStream.write(msg.getBytes());
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "데이터 전송중 오류가 발생", Toast.LENGTH_LONG).show();

        }
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
                        sendData("a");
                        Toast.makeText(MainActivity.this,"인식성공",Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    void connectToSelectedDevice(String selectedDeviceName) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.
            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            // 데이터 송수신을 위한 스트림 얻기.
            // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            // 1. 데이터를 보내기 위한 OutputStrem
            // 2. 데이터를 받기 위한 InputStream
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            // 데이터 수신 준비.
            beginListenForData();

        }catch(Exception e) { // 블루투스 연결 중 오류 발생
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();

        }
    }

    // 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
    void beginListenForData() {
        final Handler handler = new Handler();

        readBufferPosition = 0;                 // 버퍼 내 수신 문자 저장 위치.
        readBuffer = new byte[1024];            // 수신 버퍼.



    }

    // 블루투스 지원하며 활성 상태인 경우.
    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if(mPariedDeviceCount == 0 ) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();

        }
        // 페어링된 장치가 있는 경우.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {
            // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
            listItems.add(device.getName());
        }
        listItems.add("취소");  // 취소 항목 추가.


        // CharSequence : 변경 가능한 문자열.
        // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if(item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();

                }
                else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
                    connectToSelectedDevice(items[item].toString());
                }
            }

        });

        builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
        AlertDialog alert = builder.create();
        alert.show();
    }


    void checkBluetooth() {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
         이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();

        }
        else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if(!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
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
                selectDevice();
        }
    }



    // onDestroy() : 어플이 종료될때 호출 되는 함수.
    //               블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
    @Override
    protected void onDestroy() {
        try{
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }


    // onActivityResult : 사용자의 선택결과 확인 (아니오, 예)
    // RESULT_OK: 블루투스가 활성화 상태로 변경된 경우. "예"
    // RESULT_CANCELED : 오류나 사용자의 "아니오" 선택으로 비활성 상태로 남아 있는 경우  RESULT_CANCELED

    /**
     사용자가 request를 허가(또는 거부)하면 안드로이드 앱의 onActivityResult 메소도를 호출해서 request의 허가/거부를 확인할수 있다.
     첫번째 requestCode : startActivityForResult 에서 사용했던 요청 코드. REQUEST_ENABLE_BT 값
     두번째 resultCode  : 종료된 액티비티가 setReuslt로 지정한 결과 코드. RESULT_OK, RESULT_CANCELED 값중 하나가 들어감.
     세번째 data        : 종료된 액티비티가 인테트를 첨부했을 경우, 그 인텐트가 들어있고 첨부하지 않으면 null
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                    Toast.makeText(getApplicationContext(), "블루투수를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}







