package com.example.kimea.myapplication;


import android.app.Activity;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FriendTabFragment extends Fragment {

    JSONObject data = new JSONObject();
    private Socket mSocket;
    private RecyclerView fRecyclerView;
    private LinearLayoutManager fLayoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<GetFriendListItem> items;
    JSONArray fList;
    JSONObject pList;
    ArrayList userList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        String ids = getActivity().getIntent().getStringExtra("id");
        try {
            data.put("email", ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("sendUser",data);
        items = new ArrayList<>();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.friend_fragment, container, false);
        mSocket.on("friendList", listener);
        fRecyclerView = view.findViewById(R.id.friend_list);

        fLayoutManager = new LinearLayoutManager(getActivity());
        fRecyclerView.setLayoutManager(fLayoutManager);

        fLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList<>();
        adapter = new FriendListAdapter(items);
        fRecyclerView.setAdapter(adapter);

        return view;
    }
    public void addProfile(String setuUserImg,String setUserNickname){
        items.add(new GetFriendListItem(setuUserImg,setUserNickname));
        adapter.notifyDataSetChanged ();
    }

    private Emitter.Listener listener2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    pList = (JSONObject) args[0];

                    Log.i("pList", pList.toString());
                    String setUserImg ="";
                    String setUserNickname ="";

                    try {

                        setUserImg = pList.getString("u_pf_img");
                        setUserNickname = pList.getString("u_nickname");
                        Log.i("nickName&img", setUserNickname+", "+setUserImg);


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    addProfile(setUserImg,setUserNickname);
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
                    fList = (JSONArray)args[0];

                    userList = new ArrayList();


                    try {
                        for(int i = 0; i<1; i++){
                            Log.i("fList length", String.valueOf(fList.length()));
                            Log.i("listener1",fList.toString());
                            JSONObject jo = fList.getJSONObject(i);
                            JSONObject data = new JSONObject();
                            Log.i("fListArray", jo.toString());
                            userList.add(fList.getJSONObject(i).getString("f_email"));
                            Log.i("msg",fList.getJSONObject(i).getString("f_email"));
                            data.put("u_email",userList.get(i).toString());
                            data.put("index",i);
                            mSocket.emit("sendFriend",data);

                        }
                        fList = new JSONArray();
                        mSocket.on("sendFriend", listener2);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }
    };
}
