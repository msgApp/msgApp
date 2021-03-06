package com.example.kimea.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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


import com.example.kimea.myapplication.adapter.FriendListAdapter;
import com.example.kimea.myapplication.item.GetFriendListItem;
import com.example.kimea.myapplication.util.ChatApplication;
import com.example.kimea.myapplication.util.DBHelper;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

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
    Cursor curso2r;
    String ids;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    private FloatingActionButton  fab1, fab2, fab;
    TextView addFriendText;
    int countItem = 0;
    int position = -1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        Log.e(TAG,"create");
        //내아이디
        helper =  new DBHelper(getActivity().getApplicationContext());
        ids = getActivity().getIntent().getStringExtra("id");
        SQLiteDatabase database = helper.getWritableDatabase();
        String sql = "select * from divice";
        Cursor cursor2 = database.rawQuery(sql, null);
        String msgToken="";
        String id = "";
        while(cursor2.moveToNext()){
            id = cursor2.getString(0);
            msgToken = cursor2.getString(2);
        }
        cursor2.close();
        database.close();
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
            db = helper.getWritableDatabase();
            String sql2 = "select * from friend";
            curso2r = db.rawQuery(sql2,null);
            while(curso2r.moveToNext()){
                String email = curso2r.getString(0);
                String nick = curso2r.getString(1);
                String img = curso2r.getString(2);
                // Log.e(TAG,"Cursor" + img);
                String text = curso2r.getString(3);
                addProfile(img, nick, text, email);
            }
            db.close();
            /*
            String pos = getArguments().getString("position");
            Log.i("Arguments position", pos);
            position = Integer.valueOf(pos);
            adapter.itemRemove(position);
            */
        }catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    public void addProfile(String setUserImg,String setUserNickname,String setProfileText, String setEmail){
        items.add(new GetFriendListItem(setUserImg,setUserNickname,setProfileText,setEmail));
        adapter.notifyDataSetChanged ();
    }
            // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.

    public void removed (int position){
        items.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, items.size());
    }
}
