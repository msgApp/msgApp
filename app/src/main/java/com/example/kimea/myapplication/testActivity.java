package com.example.kimea.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class testActivity extends AppCompatActivity implements View.OnClickListener{
    TextView putMessage;
    TextView vMsg;
    Button subBtn;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        putMessage = findViewById(R.id.putMessage);
        subBtn = findViewById(R.id.submitBtn);
        vMsg = findViewById(R.id.vMsg);
        putMessage.setOnClickListener(this);
        subBtn.setOnClickListener(this);



    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.submitBtn:
                vMsg.setText(putMessage.getText());

                /*
                JSONObject chatData = new JSONObject();
                try {
                    chatData.put("data", putMessage);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                */

                break;

        }
    }

}
