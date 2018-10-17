package com.example.kimea.myapplication;

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
import android.widget.Toast;

import java.util.ArrayList;

public class ChattingTabFragment extends Fragment{

    SQLiteDatabase db;
    DBHelper helper = new DBHelper(getActivity());
    private RecyclerView fRecyclerView;
    private LinearLayoutManager fLayoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<GetChatRoomItem> chatItems;

    Cursor cur;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        helper = new DBHelper(getActivity());
        refresh();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatting_fragment,container,false);

        fRecyclerView = view.findViewById(R.id.chatRoomList);

        fLayoutManager = new LinearLayoutManager(getActivity());
        fRecyclerView.setLayoutManager(fLayoutManager);

        fLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        chatItems = new ArrayList<>();
        adapter = new ChatRoomAdapter(chatItems);
        fRecyclerView.setAdapter(adapter);


        Log.i("채팅텝 select","select");
        db = helper.getWritableDatabase();
        String sql = "select userId from oneUser";

        cur = db.rawQuery(sql,null);
        while(cur.moveToNext()) {
            Log.i("hihi","hihi2222");
            Log.i("useIdData", cur.getString(0));
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
                addProfile(s,rs2,null);
            }

        }


        return view;
    }
    public void addProfile(String setUserId,String setLastChat,String setChatImg){
        chatItems.add(new GetChatRoomItem(setUserId,setLastChat,setChatImg));
        adapter.notifyDataSetChanged ();
    }
    public void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
