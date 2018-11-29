package com.example.kimea.myapplication;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHttpURLConnection {

    public String request(String _url, String str) throws IOException, JSONException {

        //HttpURLConnection 참조 변수
        HttpURLConnection urlConn = null;

        OutputStream os = null;


        URL url = new URL(_url);
        urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setRequestMethod("POST");
        urlConn.setRequestProperty("Cache-Control", "no-cache");
        urlConn.setRequestProperty("Content-Type", "application/json");
        urlConn.setRequestProperty("Accept", "application/json");
        urlConn.setDoOutput(true);
        urlConn.setDoInput(true);


        //outputstream 으로 json파일을 byte형태로 바꾼후 전송
        os = urlConn.getOutputStream();
        os.write(str.getBytes());
        os.flush();

        /*
        String response;
        int responseCode = urlConn.getResponseCode();
        Log.i("response", Integer.toString(responseCode));
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = urlConn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData = null;
            int nLength = 0;
            while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != 1) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();
           response = new String(byteData);
            Log.i("DATA", response);


        }
        */

        if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }else {
            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null) {
                page += line;
            }
            return page;
        }


    }
}

