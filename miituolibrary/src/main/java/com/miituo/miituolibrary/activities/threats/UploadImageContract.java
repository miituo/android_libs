package com.miituo.miituolibrary.activities.threats;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.miituo.miituolibrary.activities.api.ApiClient;
import com.miituo.miituolibrary.activities.utils.SimpleCallBack;

import java.io.IOException;

public class UploadImageContract extends AsyncTask<String, Void, Void> {

    Activity c;
    SimpleCallBack cb;
    String resp=null, url="", tok="";
    boolean status = false;
    final String UrlApi="ImageSendProcess/Array/";
    int i;
    Bitmap bit;
    //public ProgressDialog progress;

    //Constructor
    public UploadImageContract (Activity c, int i, String tok, Bitmap bit, SimpleCallBack cb) {
        this.c = c;
        this.cb = cb;
        this.i = i;
        this.bit = bit;
        this.tok = tok;
        //progress = new ProgressDialog(c);
    }

    @Override
    protected void onPreExecute() {
//        progress.setMessage("Recuperando datos...");
//        progress.setIndeterminate(true);
//        progress.setCancelable(false);
//        progress.show();
    }

    @Override
    protected Void doInBackground(String... strings) {
        ApiClient ac = new ApiClient(c);
        try{
            status = false;
            //resp = ac.getUrl(UrlApi);
            ac.UploadPhoto(i, bit, UrlApi, tok,"","0","0","");
            status = true;
        }
        catch (IOException e) {
            e.printStackTrace();
            resp = e.getMessage();
            status = false;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        if(progress!=null){
//            progress.dismiss();
//        }
        cb.run(status,resp);
    }
}

