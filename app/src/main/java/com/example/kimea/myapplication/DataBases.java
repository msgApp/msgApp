package com.example.kimea.myapplication;

import android.provider.BaseColumns;

public class DataBases {
    public static final class CreateDB implements BaseColumns{
        public static final String TOKEN = "token";
        public static final String _TABLENAME = "token";
        public static final String _CREATE =
                "create table "+_TABLENAME+"("
                        +TOKEN+" text primary key);";
    }
}
