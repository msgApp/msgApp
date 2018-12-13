package com.example.kimea.myapplication;

import android.app.Activity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChattingTabFragment extends Fragment implements ChatRoomAdapter.OnSendItem {
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
    private Animation fab_open, fab_close;
    private Socket mSocket;
    String email;

    Cursor cur;

    public static ChattingTabFragment newInstance() {
        return new ChattingTabFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        CONTEXT = getContext();
        //refresh();


       // mSocket.on("createRoom", listener);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myEmail", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);

    }



    @Override
    public void onResume() {
        super.onResume();

        chatItems.clear();
        helper = new DBHelper(getActivity());
        db = helper.getWritableDatabase();
        String sql = "select userId from oneUser";

        cur = db.rawQuery(sql, null);
        while (cur.moveToNext()) {
            SharedPreferences preferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            String badge = preferences.getString(cur.getString(0), "");
            String s = cur.getString(0);
            String[] array = s.split("@");
            String ss = array[1];
            String[] ary2 = ss.split("\\.");
            String result = array[0] + ary2[0] + ary2[1];


            String sql2 = "select ChatText,ChatRoomNickName from'" + result + "' where Chatseq = (select max(Chatseq) from '" + result + "');";
            Cursor cur2 = db.rawQuery(sql2, null);
            while (cur2.moveToNext()) {
                String rs2 = cur2.getString(0);
                String rs3 = cur2.getString(1);
                addProfile(null, rs2, null, badge, s, email, rs3);
            }

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatting_fragment, container, false);


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
                Intent intent = new Intent(getActivity(), FriendPop.class);
                startActivity(intent);
            }
        });

        helper = new DBHelper(getActivity());
        CONTEXT = container.getContext();
        fRecyclerView = view.findViewById(R.id.chatRoomList);

        fLayoutManager = new LinearLayoutManager(getActivity());
        fRecyclerView.setLayoutManager(fLayoutManager);

        fLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        chatItems = new ArrayList<>();
        adapter = new ChatRoomAdapter(chatItems, this);
        fRecyclerView.setAdapter(adapter);
        int index = chatItems.size();
        SharedPreferences pref = getActivity().getSharedPreferences("chatEmail", Context.MODE_PRIVATE);


        return view;
    }

    public void anim() {
        if (ifFabOn) {
            chattingFab1.startAnimation(fab_close);
            chattingFab1.setClickable(false);
            createRoom.setVisibility(View.INVISIBLE);
            ifFabOn = false;
        } else {
            chattingFab1.startAnimation(fab_open);
            chattingFab1.setClickable(true);
            createRoom.setVisibility(View.VISIBLE);
            ifFabOn = true;
        }
    }

    public void addProfile(String setUserId, String setLastChat, String setChatImg, String setBadge, String setRoom, String setEmail, String setRoomNickName) {
        chatItems.add(new GetChatRoomItem(setUserId, setLastChat, setChatImg, setBadge, setRoom, setEmail, setRoomNickName));
        adapter.notifyDataSetChanged();
        for (int i = 0; i < chatItems.size(); i++) {
        }
    }


    public void sendIntent(String email, String room, String roomNick) {
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        String[] splitRoomNick = roomNick.split(" ");
        if(splitRoomNick.length>1){
            intent.putExtra("email","none");
        }else{
            intent.putExtra("email", room);
        }
        intent.putExtra("roomname", room);
        intent.putExtra("roomNickName", roomNick);
        // intent.putExtra("myEmail",myEmail.getMyId());
        SharedPreferences pref = getActivity().getSharedPreferences("chatEmail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", room);
        editor.commit();
        startActivityForResult(intent, 1);
    }

    @Override
    public void deleteRoom(JSONObject jsonObject) {

        try {
            db = helper.getWritableDatabase();
            String room = jsonObject.getString("room");
            String[] array = room.split("@");
            String ss = array[1];
            String[] array2 = ss.split("\\.");
            String result = array[0] + array2[0] + array2[1];
            db.execSQL("drop table '" + result + "';");
            db.delete("oneUser", "userId = ?", new String[]{room});
            mSocket.emit("outRoom", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
