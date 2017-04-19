package com.example.muffin.youtubeapp.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GetResponseTask extends AsyncTask<Request,Void, Response> {

    @Override
    protected Response doInBackground(Request... params) {
        OkHttpClient client = new OkHttpClient();
        try {
            return client.newCall(params[0]).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
