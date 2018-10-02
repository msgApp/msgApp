package com.example.kimea.myapplication;

public class GetMessageItem {
    String name;
    String message;
    public String getName() {
        return name;
    }
    public String getMessage() {
        return message;
    }
    public GetMessageItem(String message, String name) {
        this.message = message;
        this.name = name;
    }

}
