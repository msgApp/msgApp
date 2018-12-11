package com.example.kimea.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FriendTabFragment extends Fragment {
    private  static final String TAG = "FriendTabFragment";
    JSONObject data = new JSONObject();
    SQLiteDatabase db;
    DBHelper helper;
    private Socket mSocket;
    private RecyclerView fRecyclerView;
    private LinearLayoutManager fLayoutManager;
    private FriendListAdapter adapter;
    private ArrayList<GetFriendListItem> items;
    JSONArray msg;
    JSONArray fList;
    JSONObject pList;
    ArrayList userList;
    String ids,tableResult,intentPosition;
    int ps;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    private FloatingActionButton  fab1, fab2, fab;
    TextView addFriendText;
    int countItem = 0;
    int position = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();

        //내아이디
        helper =  new DBHelper(getActivity().getApplicationContext());
        ids = getActivity().getIntent().getStringExtra("id");
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from divice";
        Cursor cursor2 = database.rawQuery(sql, null);
        String msgToken="";
        String id = "";
        while(cursor2.moveToNext()){
            id = cursor2.getString(0);
            msgToken = cursor2.getString(2);
        }
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("myEmail",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", id);
        editor.commit();

        try {
            data.put("email", ids);
            data.put("divice",msgToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("sendUser",data);
        mSocket.on("messageAfter",Lmsg);
        items = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i("createView1","createView");
        View view  = inflater.inflate(R.layout.friend_fragment, container, false);

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fab_close);
        addFriendText = view.findViewById(R.id.addFriendText);
        fab = view.findViewById(R.id.fab);
        fab1 = view.findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFabOpen) {
                    fab1.startAnimation(fab_close);
                    isFabOpen = false;
                    fab1.setVisibility(View.INVISIBLE);
                    addFriendText.setVisibility(View.INVISIBLE);

                } else {
                    fab1.startAnimation(fab_open);
                    isFabOpen = true;
                    fab1.setVisibility(View.VISIBLE);
                    addFriendText.setVisibility(View.VISIBLE);
                }
            }
        });
        fab1 = view.findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity().getApplicationContext(),AddFriendActivity.class);
                intent.putExtra("id", ids);
                startActivity(intent);
            }
        });

        fRecyclerView = view.findViewById(R.id.friend_list);

        fLayoutManager = new LinearLayoutManager(getActivity());
        fRecyclerView.setLayoutManager(fLayoutManager);

        fLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList<>();
        adapter = new FriendListAdapter(items);
        fRecyclerView.setAdapter(adapter);

        try{
            String pos = getArguments().getString("position");
            Log.i("Arguments position", pos);
            position = Integer.valueOf(pos);
            adapter.itemRemove(position);
        }catch (Exception e) {
        }
        db = helper.getWritableDatabase();
        String sql2 = "select * from friend";
        Cursor cursor = db.rawQuery(sql2,null);
        while(cursor.moveToNext()){
            String email = cursor.getString(0);
            String nick = cursor.getString(1);
            String img = cursor.getString(2);
            // Log.e(TAG,"Cursor" + img);
            String text = cursor.getString(3);
            addProfile(img, nick, text, email);
        }
        return view;
    }

    private Emitter.Listener Lmsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    msg = (JSONArray)args[0];
                    String getMSg="";
                    String nickName="";
                    String userId="";
                    String room = "";
                    String roomNick = "";

                    try {
                        for(int i=0;i<msg.length();i++){

                            String get = msg.getString(i);
                            //JSONObject gets = msg.getJSONObject(i);
                            JSONObject gets = new JSONObject(get);

                            getMSg = gets.getString("message");
                            nickName = gets.getString("nickName");
                            userId = gets.getString("email");
                            room = gets.getString("room");
                            roomNick = gets.getString("roomNickName");

                            //insert(userId,getMSg,nickName,room,roomNick);

                            ((ViewPagerActivity)ViewPagerActivity.CONTEXT).reset();

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    public void addProfile(String setUserImg,String setUserNickname,String setProfileText, String setEmail){
        items.add(new GetFriendListItem(setUserImg,setUserNickname,setProfileText,setEmail));
        adapter.notifyDataSetChanged ();
    }
    public void insert(String id,String text,String nickName,String room, String roomNickName) {
        String[] array = room.split("@");
        String ss = array[1];
        String[] ary2 = ss.split("\\.");
        // String result = array[0]+array2[0]+array2[1];
        // Log.i("result3",ary2[0]);
        tableResult = array[0]+ary2[0]+ary2[1];

        try{
            db = helper.getReadableDatabase();
            String query = "select * from '"+tableResult+"'";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            String checkTB = c.getString(0);
        }catch(Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table '"+tableResult+"'(Chatseq integer primary key autoincrement, ChatId text,ChatNickName text, ChatText text, ChatRoomNickName text,room text, type TEXT);");
        }
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
        // 데이터의 삽입은 put을 이용한다.
        values.put("ChatId", id);
        values.put("ChatNickName",nickName);
        values.put("ChatText",text);
        values.put("ChatRoomNickName", roomNickName);
        values.put("type","0");
        db.insert("'"+tableResult+"'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)

        try{
            db = helper.getReadableDatabase();
            String query = "select userId from oneUser where userId = '"+room+"';";
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            String result = cursor.getString(0);

        }catch (Exception e){
            ContentValues values1 = new ContentValues();
            values1.put("userId",room);
            db.insert("oneUser",null,values1);
        }


        Log.i("SaveCharInsert","insert");
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.

    }
    public void removed (int position){
        items.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, items.size());
    }
}
