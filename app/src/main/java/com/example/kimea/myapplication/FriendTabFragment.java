package com.example.kimea.myapplication;

import android.content.ContentValues;
import android.content.Intent;
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
    private RecyclerView.Adapter adapter;
    private ArrayList<GetFriendListItem> items;
    JSONArray msg;
    JSONArray fList;
    JSONObject pList;
    ArrayList userList;
    String ids,tableResult;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    private FloatingActionButton  fab1, fab2, fab;
    TextView addFriendText;
    int countItem = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        //내아이디
        helper =  new DBHelper(getActivity());
        ids = getActivity().getIntent().getStringExtra("id");
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from divice";
        Cursor cursor2 = database.rawQuery(sql, null);
        String msgToken="";
        while(cursor2.moveToNext()){
            Log.e(TAG ,cursor2.getString(2));
            msgToken = cursor2.getString(2);
        }

        try {
            data.put("email", ids);
            data.put("divice",msgToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("sendUser",data);
        Log.i("ids",ids);
        mSocket.on("messageAfter",Lmsg);
        mSocket.on("friendList", listener);
        items = new ArrayList<>();
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
        fab2 = view.findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("boolean",String.valueOf(isFabOpen));
                if (isFabOpen) {
                    fab1.startAnimation(fab_close);
                    fab2.startAnimation(fab_close);
                    isFabOpen = false;
                    fab1.setVisibility(View.INVISIBLE);
                    fab2.setVisibility(View.INVISIBLE);
                    addFriendText.setVisibility(View.INVISIBLE);

                } else {
                    fab1.startAnimation(fab_open);
                    fab2.startAnimation(fab_open);
                    isFabOpen = true;
                    fab1.setVisibility(View.VISIBLE);
                    fab2.setVisibility(View.VISIBLE);
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
        fab2 = view.findViewById(R.id.fab2);


        fRecyclerView = view.findViewById(R.id.friend_list);

        fLayoutManager = new LinearLayoutManager(getActivity());
        fRecyclerView.setLayoutManager(fLayoutManager);

        fLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList<>();
        adapter = new FriendListAdapter(items);
        fRecyclerView.setAdapter(adapter);

        return view;
    }


    public void addProfile(String setuUserImg,String setUserNickname,String setProfileText, String setEmail){
        items.add(new GetFriendListItem(setuUserImg,setUserNickname,setProfileText,setEmail));
        adapter.notifyDataSetChanged ();
    }

    private Emitter.Listener listener2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    pList = (JSONObject) args[0];

                    //Log.i("pList", pList.toString());
                    String setUserImg ="";
                    String setUserNickname ="";
                    String setProfileText = "";
                    String setEmail = "";

                    try {
                        setUserImg = pList.getString("u_pf_img");
                        setUserNickname = pList.getString("u_nickname");
                        setProfileText = pList.getString("u_pf_text");
                        setEmail = pList.getString("u_email");
                       // Log.i("nickName&img", setUserNickname+", "+setUserImg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    addProfile(setUserImg,setUserNickname,setProfileText,setEmail);
                }
            });
        }
    };
    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.i("countItem",String.valueOf(countItem));
                    fList = (JSONArray)args[0];

                    userList = new ArrayList();

                    if(countItem < fList.length()){
                        try {
                            for(int i = 0; i<fList.length(); i++){
                                Log.i("fList length", String.valueOf(fList.length()));
                                Log.i("listener1",fList.toString());
                                JSONObject jo = fList.getJSONObject(i);
                                JSONObject data = new JSONObject();
                                //Log.i("fListArray", jo.toString());
                                userList.add(fList.getJSONObject(i).getString("f_email"));
                                //  Log.i("msg",fList.getJSONObject(i).getString("f_email"));
                                data.put("u_email",userList.get(i).toString());
                                data.put("index",i);
                                mSocket.emit("sendProfile",data);
                                countItem++;
                            }
                            fList = new JSONArray();
                            mSocket.on("sendProfile", listener2);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }


                }
            });
        }
    };
    private Emitter.Listener Lmsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    msg = (JSONArray)args[0];
                    Log.e(TAG,"Array "+ msg);
                    String getMSg="";
                    String nickName="";
                    String userId="";
                    try {
                        for(int i=0;i<msg.length();i++){
                            String get = msg.getString(i);
                            //JSONObject gets = msg.getJSONObject(i);
                            Log.i("msg",get);
                            JSONObject gets = new JSONObject(get);

                            getMSg = gets.getString("message");
                            nickName = gets.getString("nickName");
                            userId = gets.getString("email");
                            insert(userId,getMSg,nickName);
                            Log.i("id" ,userId);
                            Log.i("msg",getMSg);
                            Log.i("name",nickName);

                        }
                    }catch (Exception e){
                        Log.e("msgERROR", String.valueOf(e));
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    public void insert(String id,String text,String nickName) {
        String[] array = id.split("@");
        String ss = array[1];
        String[] ary2 = ss.split("\\.");
        // String result = array[0]+array2[0]+array2[1];
        // Log.i("result3",ary2[0]);
        tableResult = array[0]+ary2[0]+ary2[1];
        Log.i("tableResult", tableResult);

        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
        // 데이터의 삽입은 put을 이용한다.
        values.put("ChatId", id);
        values.put("ChatNickName",nickName);
        values.put("ChatText",text);
        values.put("type","0");
        db.insert("'"+tableResult+"'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        Log.i("SaveCharInsert","insert");
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.

    }
}
