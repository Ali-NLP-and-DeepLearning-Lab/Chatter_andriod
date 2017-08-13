package com.hanium.chat.chatter_andriod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hanium.chat.chatter_andriod.csr.ToTextActivity;
import com.hanium.chat.chatter_andriod.css.ToSpeakActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn2text, btn2speak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn2text = (Button) findViewById(R.id.btn2text);
        btn2text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ToTextActivity.class));
            }
        });

        btn2speak = (Button) findViewById(R.id.btn2speak);
        btn2speak.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ToSpeakActivity.class));
            }
        });
    }

}
