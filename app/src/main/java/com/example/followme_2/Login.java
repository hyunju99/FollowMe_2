package com.example.followme_2;

import static com.example.followme_2.R.id.login_btn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    EditText username;
    EditText password;

    Button login_btn;

    int count=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //입력받은 String 저장
        password = (EditText) findViewById(R.id.editText2);
        username = (EditText) findViewById(R.id.editText1);

        login_btn = (Button) findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //username 확인




                //임의 비밀번호 1234
                if(password.getText().toString().equals("1234")){
                    //하단의 작은박스
                    Toast.makeText(getApplicationContext(), "Login...",Toast.LENGTH_SHORT).show();
                    //다음페이지로 이동
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);


                } else{
                    Toast.makeText(getApplicationContext(), "Wrong Password!",Toast.LENGTH_SHORT).show();
                    //3번이상 아웃

                    count--;
                    Toast.makeText(getApplicationContext(),"Attempts Left : "+count, Toast.LENGTH_SHORT).show();

                    if(count==0){
                        login_btn.setEnabled(false);
                    }



                }

            }
        });


    }
}