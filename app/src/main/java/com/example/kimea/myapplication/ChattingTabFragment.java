package com.example.kimea.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

public class ChattingTabFragment extends Fragment implements ChatRoomAdapter.OnSendItem{
    SQLiteDatabase db;
    DBHelper helper = new DBHelper(getActivity());
    private RecyclerView fRecyclerView;
    private LinearLayoutManager fLayoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<GetChatRoomItem> chatItems;
    private static final String TAG = "ChattingTabFragment";
    static Context CONTEXT;
    private FloatingActionButton chattingFab, chattingFab1;
    private TextView createRoom;
    private boolean ifFabOn = false;
    private Animation fab_open,fab_close;

    Cursor cur;
    public static ChattingTabFragment newInstance() {
        return new ChattingTabFragment();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        CONTEXT  = getContext();
        //refresh();

        Log.i("create","create");
    }

    @Override
    public void onResume() {
        super.onResume();
        chatItems.clear();
        Log.i("visible","visible");
        helper =  new DBHelper(getActivity());
        db = helper.getWritableDatabase();
        String sql = "select userId from oneUser";

        cur = db.rawQuery(sql,null);
        Log.i("채팅텝 select","select");
        while(cur.moveToNext()) {
            Log.i("hihi","hihi2222");
            Log.i("useIdData", cur.getString(0));
            SharedPreferences preferences = getActivity().getSharedPreferences(cur.getString(0),Context.MODE_PRIVATE);
            String badge = preferences.getString("badge_count","");
            String s = cur.getString(0);
            String[] array = s.split("@");
            String ss = array[1];
            String[] ary2 = ss.split("\\.");
            String result = array[0]+ary2[0]+ary2[1];
            Log.i("userid 값 담은 변수",s);
            Log.i("userid 값 자른 변수",result);
            String sql2 ="select ChatText from'"+result+"' where Chatseq = (select max(Chatseq) from '"+result+"');";
            Cursor cur2 = db.rawQuery(sql2,null);
            while (cur2.moveToNext()){
                String rs2=cur2.getString(0);
                Log.e(TAG,"badge: "+badge);
                addProfile(s,rs2,null,badge,s);
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.chatting_fragment,container,false);
            Log.i("createView2","createView");

            fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
            fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);

            chattingFab = view.findViewById(R.id.chattingFab);
            chattingFab1 = view.findViewById(R.id.chattingFab1);
            createRoom = view.findViewById(R.id.createRoom);

            chattingFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    anim();
                }
            });

            chattingFab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Intent intent = new Intent(getActivity(), FriendPop.class);
                    startActivity(intent);*/
                }
            });

            helper = new DBHelper(getActivity());
            CONTEXT = container.getContext();
            fRecyclerView = view.findViewById(R.id.chatRoomList);

        fLayoutManager = new LinearLayoutManager(getActivity());
        fRecyclerView.setLayoutManager(fLayoutManager);

        fLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        chatItems = new ArrayList<>();
        adapter = new ChatRoomAdapter(chatItems,this);
        fRecyclerView.setAdapter(adapter);
        int index = chatItems.size();
        SharedPreferences pref = getActivity().getSharedPreferences("chatEmail",Context.MODE_PRIVATE);
        Log.e(TAG,"id"+pref.getString("email",""));


        return view;
    }
    public void anim(){
        if(ifFabOn){
            chattingFab1.startAnimation(fab_close);
            chattingFab1.setClickable(false);
            createRoom.setVisibility(View.INVISIBLE);
            ifFabOn = false;
        }else{
            chattingFab1.startAnimation(fab_open);
            chattingFab1.setClickable(true);
            createRoom.setVisibility(View.VISIBLE);
            ifFabOn = true;
        }
    }

    public void addProfile(String setUserId,String setLastChat,String setChatImg,String setBadge,String setRoom){
        chatItems.add(new GetChatRoomItem(setUserId,setLastChat,setChatImg,setBadge,setRoom));
        adapter.notifyDataSetChanged ();
        for(int i=0;i<chatItems.size();i++){
            Log.i("list",chatItems.get(i).toString());
        }
    }
    public void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
    /*
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {

            Log.i("visible","visible");
            db = helper.getWritableDatabase();
            String sql = "select userId from oneUser";

            cur = db.rawQuery(sql,null);
            Log.i("채팅텝 select","select");
            while(cur.moveToNext()) {
                Log.i("hihi","hihi2222");
                Log.i("useIdData", cur.getString(0));
                SharedPreferences preferences = getActivity().getSharedPreferences(cur.getString(0),Context.MODE_PRIVATE);
                String badge = preferences.getString("badge_count","");
                String s = cur.getString(0);
                String[] array = s.split("@");
                String ss = array[1];
                String[] ary2 = ss.split("\\.");
                String result = array[0]+ary2[0]+ary2[1];
                Log.i("userid 값 담은 변수",s);
                Log.i("userid 값 자른 변수",result);
                String sql2 ="select ChatText from'"+result+"' where Chatseq = (select max(Chatseq) from '"+result+"');";
                Cursor cur2 = db.rawQuery(sql2,null);
                while (cur2.moveToNext()){
                    String rs2=cur2.getString(0);
                    Log.e(TAG,"badge: "+badge);
                    addProfile(s,rs2,null,badge);
                }

            }
        }
        else
        {
            Log.i("invisible","invisible");
        }
        }
        */

    public void sendIntent(String email){
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra("email", email);

        // intent.putExtra("myEmail",myEmail.getMyId());
        SharedPreferences pref = getActivity().getSharedPreferences("chatEmail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email",email);
        editor.commit();
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            Log.i("msg","はうっ~");
            if(resultCode==1){
                Log.i("msg","はうっ~!");
                String msg = data.getStringExtra("msg");
                String email = data.getStringExtra("email");
                Log.i("msg",msg);
                Log.i("email",email);
                //addProfile(email,msg,null);
                chatItems.clear();
                Log.i("visible","visible");
                db = helper.getWritableDatabase();
                String sql = "select userId from oneUser";

                cur = db.rawQuery(sql,null);
                Log.i("채팅텝 select","select");
                while(cur.moveToNext()) {
                    Log.i("hihi","hihi2222");
                    Log.i("useIdData", cur.getString(0));
                    SharedPreferences preferences = getActivity().getSharedPreferences(cur.getString(0),Context.MODE_PRIVATE);
                    String badge = preferences.getString("badge_count","");
                    String s = cur.getString(0);
                    String[] array = s.split("@");
                    String ss = array[1];
                    String[] ary2 = ss.split("\\.");
                    String result = array[0]+ary2[0]+ary2[1];
                    Log.i("userid 값 담은 변수",s);
                    Log.i("userid 값 자른 변수",result);
                    String sql2 ="select ChatText from'"+result+"' where Chatseq = (select max(Chatseq) from '"+result+"');";
                    Cursor cur2 = db.rawQuery(sql2,null);
                    while (cur2.moveToNext()){
                        String rs2=cur2.getString(0);
                        Log.e(TAG,"badge: "+badge);
                        addProfile(s,rs2,null,badge,s);
                    }

                }

            }
        }
    }
    public void reset(){

        Toast.makeText(getActivity(),"asd",Toast.LENGTH_SHORT).show();

    }
}
