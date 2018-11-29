package com.example.kimea.myapplication;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class ChangeProfileText extends AppCompatActivity{
    TextView change;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //
        String a="";
        switch (item.getItemId()){
            case R.id.resultButton:
                String text = change.getText().toString();

                SettingTabFragment fragment = new SettingTabFragment();
                Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
                bundle.putString("text", text); // key , value
                fragment.setArguments(bundle);
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeprofiltext);
        SettingTabFragment fragment = new SettingTabFragment();
        change = findViewById(R.id.changeText);
        ActionBar ab = getSupportActionBar();

        ab.setTitle("상태 메세지");
    }

}
