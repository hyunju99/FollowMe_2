package com.example.followme_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;




public class FindingWay extends AppCompatActivity {
    private TextToSpeech tts;// tts 변수 선언
    String getVoice; //음성인식받은 데이터(목적지)
    String getBluetooth; //블루투스로 받은 데이터 (출발지)

    Handler handler = new Handler();
    StringBuilder wayOutput = new StringBuilder(); //서버에서 받은 경로 저장

    private ImageView mImageView; //돌아가는 이미지
    private int nBefore = 0; //처음 각도 0
    private float azimuth; // 방위각

    //센서 data
    SensorManager manager;
    SensorEventListener listener;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_way);

        //화면 전환하자마자 음성 출력
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i ==TextToSpeech.SUCCESS ){
                    int result = tts.setLanguage(Locale.KOREAN);

                    if(result== TextToSpeech.LANG_MISSING_DATA || result== TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(FindingWay.this, "지원하지 않는 언어 입니다", Toast.LENGTH_SHORT);
                    }
                }
                //출력되는 음성
                tts.speak("목적지를 말씀해주세요", TextToSpeech.QUEUE_ADD, null);
            }
        });

        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                //음성인식 출력
                VoiceTask voiceTask = new VoiceTask();
                voiceTask.execute();

            }
        },3000); //1초 후 음성인식 실행


        //블루투스





        //블루투스 연결 X
        getBluetooth = "맥도날드";





        //서버 연동
        final String urlStr = "http://3.101.154.199/rfid?store1 = "+ getBluetooth+"& store2 = " + getVoice;
        new Thread(new Runnable(){
            @Override
            public void run(){
                request(urlStr);
            }
        }).start();

        ///////////////////////////////////////////////////////////////////////////////////////////////
        //수경님

        ///////////////////////////////////////////////////////////////////////////////////////////////










    }




    //음성인식

    public class VoiceTask extends AsyncTask<String, Integer, String> {
        String str = null;
        @Override
        protected String doInBackground(String... params){
            //TODO Auto-generated method stub
            try{
                getVoice();//
            } catch (Exception e){
                //TODO: handle exception
            }
            return str;

        }

        @Override
        protected void onPostExecute(String result){
            try {

            } catch(Exception e){
                Log.d("onActivityResult", "getImageURL exception");
            }
        }
    }

    private void getVoice(){
        Intent intent = new Intent();
        intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        String language = "ko-KR";

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        startActivityForResult(intent,2);
        //결과값 저장
    }
    //getVoice에 결과값 저장
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){ //정상호츨
            ArrayList<String> results = data //data를 result
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String str = results.get(0); //위치


            Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show(); //toast 순간적으로 찍어줌

            //음성인식 입력받은 장소 저장
            getVoice=str;

            tts.speak(getVoice+"로 길안내를 시작합니다.", TextToSpeech.QUEUE_ADD,null);

            //음성인식 test (화면출력)
            TextView textView = findViewById(R.id.textView);

            textView.setText(str); //화면 출력
        }

    }

    //서버에서 값 받아오기
    public void request(String urlStr){ //응답 결과물 저장

        try{
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();   //HttpURLConnection 객체만들기
            if(conn != null) {
                conn.setConnectTimeout(10000);  //10초간 연결 대기
                conn.setRequestMethod("GET");
                conn.setDoInput(true);  //객체에 입력 가능

                int resCode = conn.getResponseCode();   //내부적으로 웹 서버에 페이지 요청(GET 방식)
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())); //입력 데이터를 받기 위한 Reader객체 생성
                String line = null;
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    wayOutput.append(line + "\n");
                }
                reader.close();
                conn.disconnect();
            }
        } catch (Exception ex){
            println("예외 발생함: "+ ex.toString());
        }
        //응답 해서 wayOutput에 저장
        println("응답-> " + wayOutput.toString());


    }

    public void println(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView textView = findViewById(R.id.textView);
                textView.append(data + "\n");
            }
        });
    }
    // 수경님  /////////////////////////////////////////////////////////////////////////////////////////
    /*public void startSensor(View view){
        Sensor sensor1 = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //가속도 센서
        Sensor sensor2 = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); //전자기 센서
        //리스너 연결
        manager.registerListener(listener, sensor1, SensorManager.SENSOR_DELAY_UI);
        manager.registerListener(listener, sensor2, SensorManager.SENSOR_DELAY_UI);

    }

    public void stopSensor(View view) {
        manager.unregisterListener(listener);
    }
    //방위각 계산 후 텍스트로 출력
    class SensorListener implements SensorEventListener {
        //센서 값 담을 배열
        float [] accVlaue = new float[3];
        float [] magVlaue = new float[3];

        boolean isGetAcc = false;
        boolean isGetMag = false;

        String getAzimuth;

        TextView TextView = findViewById(R.id.textView);

        @Override
        public void onSensorChanged(SensorEvent event) { //센서 변화
            //센서 타입 구분
            int type = event.sensor.getType();


            switch (type) {
                case Sensor.TYPE_ACCELEROMETER: //가속 센서
                    System.arraycopy(event.values, 0, magVlaue, 0, event.values.length);
                    isGetAcc = true;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD: //자기장 센서
                    System.arraycopy(event.values, 0, magVlaue, 0, event.values.length);
                    isGetMag = true;
                    break;
            }
            //두가지 데이터를 받으면 방위값 계산
            if (isGetAcc == true && isGetMag == true) {
                //행렬 계산을 위해 사용할 배열
                float[] R = new float[9];
                float[] I = new float[9];

                SensorManager.getRotationMatrix(R, I, accVlaue, magVlaue);
                //계산된 결과(R) -> 방위값으로 환산
                float[] values = new float[3];
                SensorManager.getOrientation(R, values);
                //방위값(라디언단위) -> 각도로 변경

                azimuth = (float) Math.toDegrees(values[0]); //방위값(-180~+180)
                float pitch = (float) Math.toDegrees(values[1]); // 좌우기울기
                float roll = (float) Math.toDegrees(values[2]); // 앞뒤기울기

                if (azimuth < 0) {
                    azimuth += 360; //범위가 -180~+180이라 양수값으로 변환
                }

                //방위값에 따라 String 출력
                if (azimuth <= 45 || azimuth >= 315) { //앞쪽
                    TextView.setText("방위값 : " + azimuth + "\n" + "방향 : " + "f");
                    getAzimuth = "f";
                } else if (azimuth >= 45 && azimuth <= 135) { //오른쪽
                    TextView.setText("방위값 : " + azimuth + "\n" + "방향 : " + "r");
                    getAzimuth = "r";
                } else if (azimuth >= 135 && azimuth <= 225) { //뒤쪽
                    TextView.setText("방위값 : " + azimuth + "\n" + "방향 : " + "b");
                    getAzimuth = "b";
                } else { //왼쪽
                    TextView.setText("방위값 : " + azimuth + "\n" + "방향 : " + "l");
                    getAzimuth = "l";
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy){ //감도변화

        }
    }*/

    //이미지 회전
    public void testRotation(int i) {
        RotateAnimation ra = new RotateAnimation(
                nBefore,
                i,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        ra.setDuration(250);
        ra.setFillAfter(true);
        mImageView.startAnimation(ra);
        nBefore = i;
    }










    ////////////////////////////////////////////////////////////////////////////////////////////








}