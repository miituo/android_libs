package com.miituo.miituolibrary.activities.api;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClientCotiza extends ApiClient{

    public ApiClientCotiza(Context c) {
        super(c);
    }

    public String getIsPaySuccededToken(String Url) throws IOException {

        final String TAG = "JsonParser.java";
        InputStream in=null;
        try {
            URL url = new URL(UrlApi+Url+"");
            HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Authorization",miituoKey);
            urlConnection.connect();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                String jsonstring="";
                //Log.d(TAG, "The response is: " + statusCode);
                in=urlConnection.getErrorStream();
                jsonstring=getStringFromInputStream(in);
                try {
                    JSONObject error = new JSONObject(jsonstring);
                    throw new IOException(statusCode==402?""+statusCode:jsonstring);
                }
                catch (JSONException ex)
                {
                    throw new IOException("Error al Convertir Respuesta de Error");
                }
            }
            else {
                in = urlConnection.getInputStream();
                String jsonstring = "";
                try {
                    jsonstring = getStringFromInputStream(in);
                    if (jsonstring != null || jsonstring != "") {
                        return jsonstring;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        finally {
            if(in!=null)
            {
                in.close();
            }
        }
    }

    public String getNewReference(String Url, int policyId, int PaymentType, String token) throws IOException, JSONException {
        InputStream in=null;
        try {
            URL url = new URL(UrlApi+Url);
            HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization",token);
            urlConnection.connect();

            JSONObject sendObject=new JSONObject();
            try {
                sendObject.put("policyId",policyId);
                sendObject.put("PaymentType",PaymentType);
            }
            catch (JSONException ex)
            {
                throw new IOException(ex.getMessage());
            }
            OutputStreamWriter writter=new OutputStreamWriter(urlConnection.getOutputStream());
            writter.write(sendObject.toString());

            writter.flush();
            writter.close();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                String jsonstring="";
                in=urlConnection.getErrorStream();
                jsonstring=getStringFromInputStream(in);
                try {
                    JSONObject error = new JSONObject(jsonstring);
                    throw new IOException(error.getString(("Message")));
                }
                catch (JSONException ex){
                    throw new IOException("Error al Convertir Respuesta de Error");
                }
            }
            else {
                in = urlConnection.getInputStream();
                String jsonstring="";
                try {
                    jsonstring=getStringFromInputStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return jsonstring;
            }
        }
        finally {
            if(in!=null){
                in.close();
            }
        }
    }


}
