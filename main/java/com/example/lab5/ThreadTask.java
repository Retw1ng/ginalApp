package com.example.lab5;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ThreadTask {

    Handler thr_handler;
    final Message message = Message.obtain();

    ThreadTask(Handler main_handler){
        this.thr_handler = main_handler;
    }

    public void doSomething() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    // Имитируем высокую нагрузку
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String string = "Заполните поля регистрации!";

                message.sendingUid = 1;
                message.obj = string;
                thr_handler.sendMessage(message);
            }
        }).start();
    }

    public void checkUser(User user, DatabaseHandler db, TextView errorMessage, Handler handler, Intent intent1, Context a){
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean check = user.checkName(db);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(check){


                    errorMessage.post(new Runnable() {
                        @Override
                        public void run() {
                            //errorMessage.setText("Пользователь с таким именем уже существует!");
                            message.sendingUid = 2;
                            String msg2 = "Пользователь с таким именем уже существует!";
                            message.obj = msg2;
                            thr_handler.sendMessage(message);

                        }
                    });


                }else {
                    errorMessage.post(new Runnable() {
                        @Override
                        public void run() {
                            errorMessage.setText("Регистрация прошла успешно!");
                            errorMessage.setVisibility(View.VISIBLE);
                        }
                    });

                    Log.i("MESSAGE", "onClick: USER ADD");

                    db.addUser(user);
                    db.close();

                    intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra("user", user);
                    a.startActivity(intent1);


                }
            }
        }).start();
    }
}

