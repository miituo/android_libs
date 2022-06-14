package com.miituo.miituolibrary.activities.threats;

import android.content.Context;
import android.os.AsyncTask;

import com.miituo.miituolibrary.activities.api.ApiClient;
import com.miituo.miituolibrary.activities.utils.SimpleCallBack;

public class GetFacturasDataAsync extends AsyncTask<String, Void, Void> {
    Context c;
    SimpleCallBack cb;
    String ErrorCode="", url="", token ="";
    boolean status=false;
    int flagFactura = -1;

    public GetFacturasDataAsync(String url, Context c, SimpleCallBack cb, String token, int flagFactura){
        this.c = c;
        this.cb=cb;
        this.url=url;
        this.token = token;
        this.flagFactura = flagFactura;
    }

    @Override
    protected Void doInBackground(String... strings) {
        ApiClient client = new ApiClient(c);
        try {
            status=false;
            if (flagFactura == 0){
                ErrorCode = client.getFacturas(url, token);
            }else{
                ErrorCode = client.postFactura(url, token);
            }
            if (ErrorCode.contains("error@")) {
                status=false;
                this.cancel(true);
            } else {
                status=true;
            }
        }catch (Exception e){
            e.printStackTrace();
            ErrorCode=e.getMessage();
            status=false;
            this.cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        try {
            cb.run(status, ErrorCode);
        }catch(Exception e){
            e.printStackTrace();
            cb.run(status, ErrorCode);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cb.run(status,ErrorCode);
    }
}
