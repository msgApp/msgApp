package com.example.kimea.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimea.myapplication.adapter.ChatAdapter;
import com.example.kimea.myapplication.item.GetMessageItem;
import com.example.kimea.myapplication.util.ChatApplication;
import com.example.kimea.myapplication.util.DBHelper;
import com.example.kimea.myapplication.util.RequestHttpURLConnection;
import com.sun.mail.iap.ByteArray;

import org.apache.http.HttpConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executors;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatRoomActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ChatRoomAvtivity";

    private static final int MY_PERMISSION_CAMERA = 1111;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int REQUEST_IMAGE_CROP = 3;
    Boolean album = false;
    Boolean picture = false;
    Uri realURI,photoURI, photoURI2;
    public static Context CONTEXT;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<GetMessageItem> items;
    TextView msgInput;
    private Socket mSocket;
    String email, result, my_email, roomname, roomNick, setNickName;
    SQLiteDatabase db;
    Button sendBtn;
    DBHelper helper = new DBHelper(ChatRoomActivity.this);
    //  DBHelper helper2 =  new DBHelper(ChatRoomActivity.this, "token.db",null,1);
    JSONObject msgData = new JSONObject();
    JSONObject pushData = new JSONObject();
    Cursor cur;
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        Intent intent = getIntent();
        CONTEXT = this;
        //!!상대 이메일 입니다!!
        email = intent.getStringExtra("email");
        try {
            String[] ay = email.split("@");
            String getSplitResult = ay[1];
            String[] ay2 = getSplitResult.split("\\.");
            if (ay2[0].equals("asd")) {
                email = "1";
            }
        } catch (Exception e) {

        }

        roomname = intent.getStringExtra("roomname");
        roomNick = intent.getStringExtra("roomNickName");
        JSONObject intoChat = new JSONObject();
        try {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            intoChat.put("myEmail", pref.getString("myEmail", null));
        } catch (Exception e) {

        }
        mSocket.emit("intoChat", intoChat);
        Log.e(TAG, "ROOMDATA = " + email + " " + roomname + " " + roomNick);
        SharedPreferences pref = getSharedPreferences("chatEmail", MODE_PRIVATE);
        SharedPreferences.Editor emaile = pref.edit();
        emaile.putString("email", roomname);
        emaile.commit();
        msgInput = findViewById(R.id.message_input);
        sendBtn = findViewById(R.id.send_button);
        msgInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (msgInput.getText().toString().replace(" ", "").equals("")) {
                    sendBtn.setEnabled(false);
                } else {
                    sendBtn.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (msgInput.getText().toString().replace(" ", "").equals("")) {
                    sendBtn.setEnabled(false);
                } else {
                    sendBtn.setEnabled(true);
                }
                if (s.length() > 500) {
                    Toast.makeText(ChatRoomActivity.this, "500자 제한", Toast.LENGTH_SHORT).show();
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(500);
                    msgInput.setFilters(FilterArray);
                } else {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (msgInput.getText().toString().replace(" ", "").equals("")) {
                    sendBtn.setEnabled(false);
                } else {
                    sendBtn.setEnabled(true);
                }
            }
        });

        try {
            SharedPreferences emailBadge = getSharedPreferences("pref", MODE_PRIVATE);
            String mailBadge = emailBadge.getString(roomname, "0");
            int badgeInt = Integer.valueOf(mailBadge);
            SharedPreferences appBadge = getSharedPreferences("pref", MODE_PRIVATE);
            int apBadge = appBadge.getInt("badge", 0);
            int badgeResult = apBadge - Integer.parseInt(mailBadge);

            SharedPreferences.Editor editor = appBadge.edit();
            editor.putInt("badge", badgeResult);
            editor.commit();
            SharedPreferences.Editor editor2 = emailBadge.edit();
            editor2.putString(roomname, "0");
            editor2.commit();
            set_badge_alarm(badgeResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.i("상대방 아이디",email);.
        String[] array = roomname.split("@");
        String ss = array[1];
        String[] ary2 = ss.split("\\.");
        result = array[0] + ary2[0] + ary2[1];
        db = helper.getWritableDatabase();
        // db.execSQL("drop table '"+result+"'");
        try {
            SQLiteDatabase database = helper.getReadableDatabase();
            String sql = "select * from '" + result + "'";
            Cursor cursor2 = database.rawQuery(sql, null);
            while (cursor2.moveToNext()) {
            }

        } catch (Exception e) {
            db = helper.getWritableDatabase();
            db.execSQL("create table '" + result + "'(Chatseq integer primary key autoincrement, ChatId text,ChatNickName text, ChatText text, ChatRoomNickName text,ChatImg BLOB ,type TEXT);");
            Log.i("ChatDataBaseCreate", "create");
        }
        db.close();

        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList();
        // RecyclerView를 위해 CustomAdapter를 사용합니다.
        mAdapter = new ChatAdapter(items);
        mRecyclerView.setAdapter(mAdapter);
        // ArrayList 에 Item 객체(데이터) 넣기

        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from '" + result + "'";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int seq = cursor.getInt(0);
            String id = cursor.getString(1);
            String nickname = cursor.getString(2);
            String text = cursor.getString(3);
            String roomNickname = cursor.getString(4);
            byte[] getImg = cursor.getBlob(5);
            String type = cursor.getString(6);
           // Log.e(TAG,"SqlLite img = "+getImg+ " TYPE = "+type+" TEXT = "+text);
            try {
                if (type.equals("2")){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(getImg,0,getImg.length);
                    addMsg("me",null,2,bitmap);
                }
                if (type.equals("3")){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(getImg,0,getImg.length);
                    addMsg(nickname,null,3,bitmap);
                }
                if (type.equals("1")) {
                    addMsg(nickname, text, 1,null);
                } else if (type.equals("0")){
                    addMsg(nickname, text, 0,null);
                }
            } catch (Exception e) {

            }
        }
        database.close();
        mSocket.on("message", listener);
        scrollToBottom();
        checkPermission();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.send_button:
                String myEmail = "";
                SQLiteDatabase database = helper.getReadableDatabase();
                String sql = "select * from divice";
                Cursor cursor2 = database.rawQuery(sql, null);
                cursor2.moveToNext();
                myEmail = cursor2.getString(0);
                Log.i("ChatRoomAct-myEmail", myEmail);
                addMsg("me", msgInput.getText().toString(), 1,null);
                //mAdapter.notifyItemInserted(items.size());
                mAdapter.notifyDataSetChanged();

                //채팅방 목록 테이블에 존재하는지 확인
                db = helper.getWritableDatabase();
                String sql3 = "select userID from oneUser where userId = '" + roomname + "';"; //where userId = '"+email+"';";
                cur = db.rawQuery(sql3, null);
                if (cur.moveToFirst()) {
                    Log.e(TAG, "ChatRoomActivity break check " + roomname);
                } else {
                    Log.e(TAG, "ChatRoomActivity insert2 check " + roomname);
                    insert2(roomname);
                }
                insert("me", "me", msgInput.getText().toString(), "1", roomNick,null);
                emitMsg(msgInput.getText().toString(),"text");
                msgInput.setText("");
                scrollToBottom();
                database.close();
                db.close();
                break;
            case R.id.message_input:
                scrollToBottom();
                break;
            case R.id.message_menu:
                CharSequence info[] = new CharSequence[] {"갤러리에서 불러오기", "카메라로 찍기" };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("사진 보내기");
                builder.setItems(info, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which)
                        {
                            case 0:
                                doTakeAlbumAction();
                                break;
                            case 1:
                                sendTakePhotoIntent();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();

                break;
        }

    }
    public void emitMsg(String text,String result){
        msgData = new JSONObject();
        pushData = new JSONObject();
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String myEmail =  pref.getString("myEmail", null);
        try {

            msgData.put("message", text);
            pushData.put("message", text);
            msgData.put("u_email", email);
            Log.e(TAG, "RoomName = " + email);
            msgData.put("my_email", myEmail);
            msgData.put("room", roomname);
            Log.e(TAG, "RoomName = " + roomname);
            if (email.equals(roomname)) {
                pushData.put("roomname", myEmail);
            } else {
                pushData.put("roomname", roomname);
            }
            pushData.put("u_email", myEmail);
            pushData.put("f_email", email);
            pushData.put("isPicture",false);
            //   pushData.put("room",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("pushMsg", pushData);
        mSocket.emit("sendMsg", msgData);
    }
    public void addMsg(String setName, String setMsg, int type, Bitmap bitmap) {
        if (type == 0) {
            items.add(new GetMessageItem.Builder(GetMessageItem.TYPE_MESSAGE).username(setName).userMessage(setMsg).build());
            //insert(setName,setMsg,"0");
        } else if(type == 1) {
            items.add(new GetMessageItem.Builder(GetMessageItem.TYPE_MYMSG).username(setName).userMessage(setMsg).build());
            //insert(setName,setMsg,"1");
        }
        else if(type == 2) {
            items.add(new GetMessageItem.Builder(GetMessageItem.TYPE_MYIMG).username(setName).userMessage(setMsg).userBitmap(bitmap).build());
            //insert(setName,setMsg,"1");
        }
        else if(type == 3) {
            items.add(new GetMessageItem.Builder(GetMessageItem.TYPE_IMG).username(setName).userMessage(setMsg).userBitmap(bitmap).build());
            //insert(setName,setMsg,"1");
        }
        // mAdapter.notifyItemInserted(items.size());
        mAdapter.notifyDataSetChanged();
        scrollToBottom();
    }
    public void reset(final String nick, final Bitmap bitmaps){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // 해당 작업을 처리함
                        addMsg(nick,"사진",3,bitmaps);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
        Log.e(TAG,"GET RESET BITMAP = "+bitmaps);
    }
    private void scrollToBottom() {
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    //socket 데이터 받아온값 처리
    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject msg = (JSONObject) args[0];
                    Iterator i = msg.keys();
                    ArrayList<String> keys = null;
                    ArrayList<String> values = null;
                    String setName = "";
                    setNickName = "";
                    String setMsg = "";
                    String setRoom = "";
                    try {
                        setMsg = msg.getString("\"message\"");
                        setName = msg.getString("\"email\"");
                        setNickName = msg.getString("\"nickName\"");
                        setRoom = msg.getString("\"room\"");

                        String[] getMsg = setMsg.split("_");
                        if (getMsg[0].equals("MsgApp")&&roomname.equals(setRoom)) {
                            String url = "http://122.40.72.34:1300/download";
                            ChatRoomActivity.ServerTask serverTask = new ChatRoomActivity.ServerTask(setMsg, url, "download");
                            serverTask.execute();
                        }
                        if (roomname.equals(setRoom)&&!getMsg[0].equals("MsgApp")) {
                                addMsg(setNickName, setMsg, 0,null);
                            }
                            //insert(setName,setNickName,setMsg,"0");

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    //상대방 이메일이 맞으면 테이블 인서트

                }
            });
        }
    };


    public String userId() {
        db = helper.getReadableDatabase();
        String query = "select user from divice";
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        String user = cur.getString(0);
        db.close();
        return user;
    }

    public void outRoom(JSONObject jsonObject) {
        mSocket.emit("outRoom", jsonObject);
    }

    //데이터 삽입
    public void insert(String id, String nickName, String text, String type, String roomNickName, byte[] img) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
        Log.e(TAG, "ChatRoomActivity-insert-roomNickName " + roomNickName);

        // 데이터의 삽입은 put을 이용한다.
        values.put("ChatId", id);
        values.put("ChatNickName", nickName);
        values.put("ChatText", text);
        values.put("ChatRoomNickName", roomNickName);
        values.put("type", type);
        values.put("ChatImg",img);

        db.insert("'" + result + "'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        Log.e(TAG,"-----------insert----------");
        db = helper.getReadableDatabase();
        String sql = "select ChatRoomNickName from '" + result + "';";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        db.close();
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
    }

    //채팅방 유저 확인 테이블 삽입
    public void insert2(String id) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values2 = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.
        values2.put("userId", id);
        db.insert("oneUser", null, values2); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from oneUser;";
        Cursor cursor2 = database.rawQuery(sql, null);

        while (cursor2.moveToNext()) {
        }
        db.close();
        database.close();
    }

    //채팅방 목록 갱신시 필요한 데이터
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        JSONObject actData = new JSONObject();
        db = helper.getReadableDatabase();
        String query2 = "select user from divice";
        Cursor cur3 = db.rawQuery(query2, null);
        cur3.moveToFirst();
        my_email = cur3.getString(0);
        try {
            actData.put("email", my_email);
            //actData.put("activity", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("outActivity", actData);

        Intent intent = new Intent();
        String rs2 = "";
        db.close();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences("chatEmail", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", "none");
        editor.commit();
    }
    public void set_badge_alarm(int badge_count) {
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", badge_count);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", "com.example.kimea.myapplication.LoadingActivity");
        sendBroadcast(intent);
    }


                                        /*----------------사진 관련----------------*/

    String mCurrentPhotoPath;
    private void sendTakePhotoIntent() {
        //카메라 호출
        album = false;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile();
            Log.e(TAG,"File = "+photoFile);
            photoURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
            realURI = Uri.fromFile(photoFile);
            Log.e(TAG,"File2 = "+photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            Log.e(TAG,"photoURI = "+photoURI);
        }
    }
    private File createImageFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MsgApp_" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory()+ "/Pictures", "MsgApp");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }
        File imageFile = new File(storageDir, imageFileName);
        return imageFile;

        // 특정 경로와 폴더를 지정하지 않고, 메모리 최상 위치에 저장 방법
    }
    private File downloadImg(){
        File storageDir = new File(Environment.getExternalStorageDirectory()+ "/Pictures", "MsgApp");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        return storageDir;

        // 특정 경로와 폴더를 지정하지 않고, 메모리 최상 위치에 저장 방법
    }
    private void doTakeAlbumAction()
    {
        // 앨범 호출
        album = true;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }
    private void cropImage() {
        picture = true;
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기
        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
        //cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율
        //cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        cropIntent.putExtra("scale", true);
        photoURI2 = Uri.fromFile(createImageFile());
        cropIntent.putExtra("output", photoURI2);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int check = 0;
        if (resultCode != RESULT_OK) {
           // Toast.makeText(getApplicationContext(), "onActivityResult : RESULT_NOT_OK", Toast.LENGTH_LONG).show();
        } else {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: // 앨범 이미지 가져오기
                    photoURI = data.getData(); // 앨범 이미지의 경로
                    Log.e(TAG,"gallary get picture = "+ photoURI);
                    // break; REQUEST_IMAGE_CAPTURE로 전달하여 Crop
                case REQUEST_IMAGE_CAPTURE:
                    cropImage();
                    break;
                case REQUEST_IMAGE_CROP:
                    Log.e(TAG,"---------------Crop Photo----------------");
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    if (album){
                        options.inSampleSize = 5;
                    }else{
                        options.inSampleSize = 3;
                    }
                    Log.e(TAG,"GetPath = "+ photoURI2.getPath());
                    Bitmap photo = BitmapFactory.decodeFile(photoURI2.getPath(),options);
                    byte[] getByte = getByteArrayFromDrawable(photo);
                    Log.e(TAG,"Photo = "+ photo);
                    Log.e(TAG,"getByte = "+getByte.toString());

                    String url = "http://122.40.72.34:1300/upload";
                    ChatRoomActivity.ServerTask serverTask = new ChatRoomActivity.ServerTask(photoURI2.getPath(),url,"upload");
                    serverTask.execute();

                    //Bitmap resized = Bitmap.createScaledBitmap(photo, 255, 255, true);
                    addMsg("me",null,2,photo);
                    Log.e(TAG,"---------------Go Insert----------------");
                    insert("me", "me", "사진", "2", roomNick, getByte);
                    //encodeImg.length()
                    File photo2 = new File(photoURI2.getPath());
                    if (!album){
                        File photo1 = new File(realURI.getPath());
                        if (photo1.exists()){
                            photo1.delete();
                        }
                    }
                    if (photo2.exists()){
                        photo2.delete();
                    }
                    //checkPicture.setImageBitmap(photo);
                    Log.e(TAG,"CropPhoto = " +photoURI.getPath()+" "+photoURI2.getPath());
                    break;
            }
        }
    }

    public class ServerTask extends AsyncTask<Void,Void,Bitmap> {
        private String Data;
        private String url;
        private String Check;
        public ServerTask(String Data, String url, String Check){
            this.Data = Data;
            this.url = url;
            this.Check = Check;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            Bitmap bitmap = null;
            try{
                if (Check.equals("upload")){
                File file = new File(Data);
                DataOutputStream dos;
                if (file.isFile()){
                    FileInputStream mFileInputStream = new FileInputStream(file);
                    URL connectUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(7000);
                    conn.setReadTimeout(7000);
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "Multipart/form-data");
                    conn.setRequestProperty("Accept-Encoding", "gzip");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", Data);


                    Log.e("file"," "+Data);
                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + Data  + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    Log.e(TAG,"LineEnd = "+lineEnd);

                    int bytesAvailable = mFileInputStream.available();
                    int maxBufferSize = 1024 * 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                    // read image
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = mFileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    mFileInputStream.close();
                    Log.e(TAG,"dos ="+dos.toString());
                   // Log.e(TAG,"dos ="+Conva -);
                    dos.flush(); // finish upload...
                    Log.e(TAG,"UploadFinish");

                    InputStream tmp = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(tmp));
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    String log = stringBuffer.toString();
                    emitMsg(log, "picture");
                    Log.e("LOG"," "+log);
                    mFileInputStream.close();
                    dos.close();
                    //conn.disconnect();
                    bitmap = null;
                    }
                }
                /*else if (Check.equals("download")){
                    Log.e("DOWNLOAD", Data);
                    URL connectUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");

                    OutputStream out = conn.getOutputStream();
                    JSONObject jsonObject = new JSONObject();
                    try{
                        jsonObject.put("filename",Data);
                    }catch (Exception e){

                    }
                    out.write(jsonObject.toString().getBytes()); // 출력 스트림에 출력.
                    Log.e("ASDASDAS"," "+jsonObject.getString("filename"));
                    out.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
                    out.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

                    InputStream tmp = conn.getInputStream();
                    File file = new File(downloadImg(),Data);
                    OutputStream outputStream = new FileOutputStream(file);
                    byte data[] = new byte[1024*1024];
                    int res = 1;
                    // write to file's outputStream
                    while ((res = tmp.read(data)) > 0) {
                        // Don't use outputStream.write(data) !!!
                        outputStream.write(data, 0, res);
                    }
                    // clean
                    outputStream.flush();
                    outputStream.close();
                    tmp.close();
                    conn.disconnect();

                    if (file.exists()){
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize= 3;
                        bitmap  = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
                        Log.e(TAG, "GetPicture to Bitmap = " +bitmap);
                    }
                }
                    */
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        /*
        @Override
        protected void onPostExecute(Bitmap  bitmap) {
            super.onPostExecute(bitmap);
            Log.e(TAG,"postExcute = "+bitmap+" name = "+setNickName);
          //  addMsg(setNickName,"사진",3,bitmap);
            //rereset();
        }
        */
    }
    public byte[] getByteArrayFromDrawable(Bitmap d) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        d.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        return data;
    }
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        //Toast.makeText(ChatRoomActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..
                break;
        }
    }
}
