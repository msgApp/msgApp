package com.example.kimea.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimea.myapplication.util.DatePickerFragment;
import com.example.kimea.myapplication.util.RequestHttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity{
    TextView email;
     TextView emailCheck;
    TextView password;
    TextView passCheck;
    TextView nickName;
    TextView rName;
    TextView birthDay;
    CheckBox male;
    CheckBox female;
    String check;
    String result;
    String url;
    int emailResult;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        birthDay = findViewById(R.id.birthDay);
        email = findViewById(R.id.re_email);
        emailCheck = findViewById(R.id.re_emailCheck);
        password = findViewById(R.id.re_passwd);
        passCheck = findViewById(R.id.re_passCheck);
        nickName = findViewById(R.id.re_nickName);
        rName = findViewById(R.id.re_rName);
        male = findViewById(R.id.re_male);
        female = findViewById(R.id.re_female);

        passCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pw = password.getText().toString();
                String pwCheck = passCheck.getText().toString();

                if(pw.equals(pwCheck)){
                    password.setBackgroundColor(Color.GREEN);
                    passCheck.setBackgroundColor(Color.GREEN);
                }else if(!pw.equals(pwCheck)){
                    password.setBackgroundColor(Color.RED);
                    passCheck.setBackgroundColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // 입력이 끝났을 때
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String result = dateFormat.format(today);

        birthDay.setText(result);
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.reSubmitBtn:
                if (email.getText().toString().length()==0){
                    Toast.makeText(RegisterActivity.this,"이메일을 입력하세요!",Toast.LENGTH_LONG).show();
                    email.requestFocus();
                    return;
                }else if(password.getText().toString().length()==0){
                    Toast.makeText(RegisterActivity.this,"비밀번호를 입력하세요!",Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }else if (passCheck.getText().toString().length()==0){
                    Toast.makeText(RegisterActivity.this,"비밀번호를 확인해주세요!",Toast.LENGTH_SHORT).show();
                    passCheck.requestFocus();
                    return;
                }else if(!password.getText().toString().equals(passCheck.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                    passCheck.setText("");
                    passCheck.requestFocus();
                    return;
                }else if (nickName.getText().toString().length()==0){
                    Toast.makeText(RegisterActivity.this,"닉네임을 입력해주세요!",Toast.LENGTH_SHORT).show();
                    passCheck.requestFocus();
                    return;
                }else if (rName.getText().toString().length()==0){
                    Toast.makeText(RegisterActivity.this,"본명을 입력해주세요!",Toast.LENGTH_SHORT).show();
                    passCheck.requestFocus();
                    return;
                }
                if (isValidEmail(email.getText().toString())==false){
                    Toast.makeText(RegisterActivity.this,"이메일 형식을 맞춰",Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }else if(emailResult==0){
                    Toast.makeText(RegisterActivity.this,"이메일 인증을 해주세요!",Toast.LENGTH_SHORT).show();
                    emailCheck.requestFocus();
                    return;
                }
                
                JSONObject reData = new JSONObject();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Drawable drawable = getResources().getDrawable(R.drawable.default_profile);
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] photo = baos.toByteArray();
                String encodeImg = Base64.encodeToString(photo, Base64.DEFAULT);
                try{
                    reData.put("img",encodeImg);
                    reData.put("u_email",email.getText().toString());
                    reData.put("u_passwd",password.getText().toString());
                    reData.put("u_nickname",nickName.getText().toString());
                    reData.put("u_name",rName.getText().toString());
                    reData.put("u_sex",check);
                    reData.put("u_birthday",birthDay.getText().toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                url = "http://122.40.72.34:1300/signUp";
                ServerTask serverTask = new ServerTask(url,reData.toString());
                serverTask.execute();

                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(result.equals("signUp success")){
                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            intent.putExtra("first","first");
                            startActivity(intent);
                        }
                    }
                }, 500);

                break;
            case R.id.re_male:
                female.setChecked(false);
                check = "남성";
                break;
            case R.id.re_female:
                male.setChecked(false);
                check="여성";
                break;
            case R.id.mailSend:

                url = "http://122.40.72.34:1300/overlapCheck";

                JSONObject jemail = new JSONObject();
                try{
                    jemail.put("u_email",email.getText().toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }

                serverTask = new ServerTask(url,jemail.toString());
                serverTask.execute();
                break;
            case R.id.re_reEmailCheck:
                if (emailCheck.getText().toString().equals(result)){
                    Toast.makeText(RegisterActivity.this,"인증성공!",Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    emailResult=1;
                }else{
                    Toast.makeText(RegisterActivity.this,"인증실패!",Toast.LENGTH_SHORT).show();
                    emailCheck.setText("");
                    emailCheck.requestFocus();
                    emailResult=0;
                }
                break;
        }
    }
    public void onBirthdayClicked (View v) {
        android.support.v4.app.DialogFragment newFragment = new DatePickerFragment();   //DatePickerFragment 객체 생성
        newFragment.show(getSupportFragmentManager(), "datePicker");                //프래그먼트 매니저를 이용하여 프래그먼트 보여주기
    }
    public void getDate(String date){
        birthDay.setText(date);
    }

    public class ServerTask extends AsyncTask<Void,Void,String> {
        private String url;
        private String str;
        public ServerTask(String url, String str){
            this.url = url;
            this.str = str;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            try {
                result = requestHttpURLConnection.request(url,str);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            result = s;
            if(result.equals("[\"signUp success\"]")) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
            emailCheck.setText(s);

        }
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
