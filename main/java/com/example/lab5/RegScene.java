package com.example.lab5;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.logging.LogRecord;

public class RegScene extends Activity {
    TextView changeTextPrev;
    TextView errorMessage;
    Button mainBtn;
    EditText inputName;
    EditText inputPassword;
    User user;
    DatabaseHandler db = new DatabaseHandler(this);
    final Looper looper = Looper.getMainLooper();
    final Message message = Message.obtain();

    //String string = new String();

    final Handler handler = new Handler(looper) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.sendingUid == 1) {

                errorMessage.setText((String) msg.obj);
            }
            if (msg.sendingUid == 2) {
                errorMessage.setText((String) msg.obj);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enter_scene);
        final Context a = RegScene.this.getApplicationContext();

        changeTextPrev = findViewById(R.id.enterTEXT);
        mainBtn = findViewById(R.id.regenterBtn);

        inputName = findViewById(R.id.inputName);
        inputPassword = findViewById(R.id.inputPassword);

        errorMessage = findViewById(R.id.errorMessage);

        //Intent intent = new Intent(this, InitScene.class);
        Bundle changeMode = this.getIntent().getExtras();

        int flag = changeMode.getInt("mode");
        Intent intent1 = new Intent(this, MainScene.class);

        if(flag == 1){
            changeTextPrev.setText("Регистрация");
            mainBtn.setText("Регистрация");

            mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputName.getText().toString().trim().equals("") || inputPassword.getText().toString().trim().equals("")) {
                    errorMessage.setText("Проверка...");
                    errorMessage.setVisibility(View.VISIBLE);
                    new ThreadTask(handler).doSomething();


                }else {
                    user = new User(inputName.getText().toString().trim(), inputPassword.getText().toString().trim());
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Проверка...");
                    new ThreadTask(handler).checkUser(user, db, errorMessage, handler, intent1, a);
                }
            }
            });
        }else {
            mainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User logUser = new User(inputName.getText().toString().trim(), inputPassword.getText().toString().trim());
                    if(logUser.getLogin().equals("") || logUser.getPass().equals("")) {
                        errorMessage.setText("Заполните поля!");
                        errorMessage.setVisibility(View.VISIBLE);
                    }else if(CheckUser(logUser)){
                        db.close();
                        intent1.putExtra("user", logUser);
                        startActivity(intent1);
                    }else{
                        errorMessage.setText("Неверный логин или пароль!");
                        errorMessage.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public boolean CheckUser(User user) {
        return db.selectUserData(user);
    }
}
