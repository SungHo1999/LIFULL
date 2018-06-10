# LIFULL
<README>
퍼미션체크를 하기 위해 TedPermission SDK를 이용하였습니다. 
자세한 사용법은 https://github.com/ParkSangGwon/TedPermission 을 이용하여 사용법을 숙지해주세요.

stt라이브러리를 사용하기 위한 초기값으로 Intent 변수에 선언을 해준다.

//내부에있는 stt클래스를 불러온다.
i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

//불러온 stt클래스에 패키지를 선언해주고 인식할 언어를 설정해준다.
i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

//onPartialResults을 받기위한 초기값        
i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);

//인식 인자를 생성한다.
mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);      
//인식 인자의 리스너를 연결해준다.
mRecognizer.setRecognitionListener(listener);       \

getDeviceFromBondedList 은 연결가능한 블루투스 디바이스에 대한 정보를 가져온다. 

sendData 블루투스가 연결되어있다면 전송소켓인 OutPutStream으로 전송할 데이터를 전송하게 된다.
만일 연결되어있지 않거나 오류가 발생하게되면 catcah문이 실행되어 데이터 전송중 오류 발생이라는 토스트 메세지를 보여준다.

RecognitionListener listener
//stt가 인식할때 사용할 리스너이다.
onRmsChanged 일정 사운드레벨이 되면 음성인식을 중단한다 설정하지 않으면 디폴트값인 -2로 설정된다.

onResults 결과값이 들어오게 된다.

onReadyForSpeech 준비가 다 된 SpeechRecognizer의 startListening(Intent recognizerIntent)을 호출하면 실행되며
음성인식을 할 준비가 되었다는 콜백입니다.

onPartialResults 부분적으로 인식된 값을 지속적으로 반화하며 onResult처럼 최종보정을 받지 않았기때문에 매끄럽지는 않습니다.
     
onError 에러가 발생했을 때 호출됩니다. 전달인자 error은 SpeechRecognizer 에서 미리 정의된 다음의 상수들입니다.

1 ERROR_NETWORK_TIMEOUT : 네트워크 타임아웃

2 ERROR_NETWORK :  그 외 네트워크 에러

3 ERROR_AUDIO :  녹음 에러

4 ERROR_SERVER :  서버에서 에러를 보냄

5 ERROR_CLIENT :  클라이언트 에러

6 ERROR_SPEECH_TIMEOUT :  아무 음성도 듣지 못했을 때

7 ERROR_NO_MATCH :  적당한 결과를 찾지 못했을 때

8 ERROR_RECOGNIZER_BUSY :  RecognitionService가 바쁠 때

9 ERROR_INSUFFICIENT_PERMISSIONS: uses-permission(즉 RECORD_AUDIO) 이 없을 때

     

onEndOfSpeech 음성이 끝났을 때 호출됩니다. 음성 인식이 성공했다는 건 아니고 이 콜백 다음에 인식 결과에 따라 on‌Error나 onResults가 호출됩니다.

onBufferReceived 새 소리가 들어왔을 때 호출됩니다. 직접 확인해 보면 onReadyForSpeech과 onEndOfSpeech 사이에서 무수히 호출되는 것을 볼 수 있습니다.

onBeginningOfSpeech 음성이 입력되기 시작하면 호출된다.

mHdrVoiceRecoState 음성인식의 동작이 끝날시 재시작의 유무를 판단한다,.

receiveResults onPartialResults에서 반환된 값들을 판단하고 설정된 키워드면 sendData를 통해 아두이노에 
진동을 울리라는 메세지를 보낸다.

connectToSelectedDevice 블루투스에 디바이스를 연결하기 위한 메소드로 
createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.           
이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.

이후 데이터를 보내고 받는 In ,Out 스트림을 열어준다. 

selectDevice 는 getBondedDevices에서 얻어온 연결가능한 장비들의 리스트를 띄워주고 선택시 
connectToSelectedDevice가 실행되어 블루투스가 연결되고 취소버튼 선택시 연결할 장치를 선택하지 않았다는 토스트 메세지를
보내준다.

checkBluetooth()
만약 기기가 블루투스 미지원 기기일 경우 "기기가 블루투스를 지원하지 않습니다"라는 메세지를 출력한다.
블루투스를 지원하는 기기일경우 블루투스 모듈이 활성화 되었는지 확인 후
활성 상태일 경우 selectDevice를 호출해 장치를 선택할 수 있도록한다.
만약 블루투스가 비활성 상태일 경우 "현재 블루투스가 비활성 상태입니다"라는 메시지를 출력하고 나서 블루투스 활성화 여부를 물어본다.
"예"를 선택하면 블루투스 장치를 활성화 시키고
"아니오"를 선택하면 비활성화 상태를 유지한다.

onDestroy()
어플이 종료될때 호출 되는 함수로 블루투스 연결이 필요하지 않는 경우
입출력 스트림 소켓을 닫아준다.

onActivityResult
사용자의 선택결과를 확인하기 위한 함수
사용자가 request를 허가 또는 거부하면 이 메소드를 호출하여 request의 허가/거부결과를 확인할 수 있다
블루투스가 할성화 상태로 변경된 경우 RESULT_OK값이 되고 selectDevice를 호출함
비활성화일 경우 Result_CANCELED로 "블루투스를 사용할 수 없어 프로그램을 종료합니다"라는 메시지와 함께 종료됨
