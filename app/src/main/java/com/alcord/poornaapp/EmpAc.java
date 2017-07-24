package com.alcord.poornaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class EmpAc extends AppCompatActivity {


    Helper helper = new Helper(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp);

        TextView textView = (TextView) findViewById(R.id.test);

        try {
            helper.createDataBase();

        } catch (IOException e) {
            e.printStackTrace();
        }



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(EmpAc.this,MainActivity.class));
            }
        });

    }
}
