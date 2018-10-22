package com.example.kimea.myapplication;

public class GetMessageItem {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_MYMSG = 1;
    private int mType = -1;
    private String name;
    private String message;

    private  GetMessageItem(){};
    public int getmType(){return mType;}
    public String getName() {
        return name;
    }
    public String getMessage() {
        return message;
    }

    public static class Builder {

        private final int mType;
        private String name;
        private String message;

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

        public GetMessageItem build() {
            GetMessageItem gcr = new GetMessageItem();
            gcr.mType = mType;
            gcr.name = name;
            gcr.message = message;
            return gcr;
        }
    }

}
