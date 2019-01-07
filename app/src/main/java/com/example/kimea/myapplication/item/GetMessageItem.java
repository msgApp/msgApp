package com.example.kimea.myapplication.item;

import android.graphics.Bitmap;

public class GetMessageItem {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_MYMSG = 1;
    public static final int TYPE_MYIMG = 2;
    public static final int TYPE_IMG  = 3;

    private int mType = -1;
    private String name;
    private String message;
    private Bitmap bitmap;

    private  GetMessageItem(){};
    public int getmType(){return mType;}
    public String getName() {
        return name;
    }
    public String getMessage() {
        return message;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public static class Builder {

        private final int mType;
        private String name;
        private String message;
        private Bitmap bitmap;

        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            name = username;
            return this;
        }

        public Builder userMessage(String userMessage) {
            message = userMessage;
            return this;
        }
        public Builder userBitmap(Bitmap userBitmap){
            bitmap = userBitmap;
            return this;
        }

        public GetMessageItem build() {
            GetMessageItem gcr = new GetMessageItem();
            gcr.mType = mType;
            gcr.name = name;
            gcr.message = message;
            gcr.bitmap = bitmap;
            return gcr;
        }
    }

}
