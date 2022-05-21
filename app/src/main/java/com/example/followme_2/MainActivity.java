package com.example.followme_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;// tts 변수 선언
    Button findway_btn ,emergencycall_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //화면 전환하자마자 음성 출력
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i ==TextToSpeech.SUCCESS ){
                    int result = tts.setLanguage(Locale.KOREAN);

                    if(result== TextToSpeech.LANG_MISSING_DATA || result== TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(MainActivity.this, "지원하지 않는 언어 입니다", Toast.LENGTH_SHORT);
                    }
                }
                //출력되는 음성
                tts.speak("길찾기를 원하시면 상단 버튼을, 긴급 전화를 원하시면 하단 버튼을 눌러주세요", TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        findway_btn = (Button) findViewById(R.id.findway_btn); // 길찾기 버튼
        emergencycall_btn = (Button) findViewById(R.id.emergencycall_btn); //긴급전화 버튼


        //길찾기 버튼 눌렀을 경우
        findway_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //길찾기 class로 이동
                Intent intent = new Intent(MainActivity.this, FindingWay.class);
                startActivity(intent);

            }
        });

        //긴급전화 버튼 눌렀을 경우
        emergencycall_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //음성출력
                tts.speak("119로 전화하겠습니다", TextToSpeech.QUEUE_FLUSH, null);

                Intent callintent = new Intent(Intent.ACTION_CALL);
                callintent.setData(Uri.parse("tel119"));
                callintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callintent);


            }
        });






    }
}