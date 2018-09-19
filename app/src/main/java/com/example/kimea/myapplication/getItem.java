package com.example.kimea.myapplication;

public class getItem {
    String name;
    String message;
    public String getName() {
        return name;
    }
    public String getMessage() {
        return message;
    }
    public getItem(String message, String name) {
        this.message = message;
        this.name = name;
    }

}
