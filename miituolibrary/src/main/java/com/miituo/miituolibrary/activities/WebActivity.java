package com.miituo.miituolibrary.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.miituo.miituolibrary.R;
import com.miituo.miituolibrary.activities.api.ApiClientCotiza;
import com.miituo.miituolibrary.activities.data.IinfoClient;
import com.miituo.miituolibrary.activities.utils.SimpleCallBack;

import org.json.JSONObject;

public class WebActivity extends AppCompatActivity {

    private WebView wvPago;
    private String referenciaPago = "";
    private boolean cambioExitoso=false;
    private LinearLayout cntTarjetas,cntAmex;
    public int tipoPago=1;

    //Alarma a = new Alarma();
    AlertDialog alertToClose;

    public static AlertDialog alertaPago;
    public static boolean ispagoshown = false;
    public static boolean ispagoshown2 = false;

    private int mInterval = 5000;
    private Handler mHandler;

    private SharedPreferences app_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_web);
        final View root = findViewById(android.R.id.content); // <-- content view
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cambiar método de pago");
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        wvPago = (WebView) findViewById(R.id.wvPago);
        //cntTarjetas=(LinearLayout)findViewById(R.id.cntTarjetas);
        //cntAmex=(LinearLayout)findViewById(R.id.cntAmex);

        app_preferences = getSharedPreferences("miituo", Context.MODE_PRIVATE);

        mHandler = new Handler();
        startRepeatingTask();

//        cntTarjetas.setOnClickListener(v -> {
//            if(cntTarjetas.getBackground()!=getResources().getDrawable(R.drawable.blueborder)) {
//                cntTarjetas.setBackground(getResources().getDrawable(R.drawable.blueborder));
//                cntAmex.setBackground(getResources().getDrawable(R.drawable.border));
//                tipoPago=3;
//                showUrl();
//            }
//        });
//        cntAmex.setOnClickListener(v -> {
//            if(cntAmex.getBackground()!=getResources().getDrawable(R.drawable.blueborder)) {
//                cntAmex.setBackground(getResources().getDrawable(R.drawable.blueborder));
//                cntTarjetas.setBackground(getResources().getDrawable(R.drawable.border));
//                tipoPago=1;
//                showUrl();
//            }
//        });
        showUrl();
    }

    public void showUrl() {
        new getReference(IinfoClient.getInfoClientObject().getClient().getId(), IinfoClient.getInfoClientObject().getPolicies().getId(), tipoPago, new SimpleCallBack() {
            @Override
            public void run(boolean status, String res) {
                if (status) {
                    try {
                        JSONObject object = new JSONObject(res);
                        String url = object.getString("Url");
                        referenciaPago = object.getString("Reference");
                        wvPago = (WebView) findViewById(R.id.wvPago);
                        wvPago.loadUrl(url);
                        wvPago.setVisibility(View.VISIBLE);
                        wvPago.setWebViewClient(new WebViewClient());
                        wvPago.getSettings().setJavaScriptEnabled(true);

//                    if(a != null){
//                        a.cancelAlarm(WebActivity.this);
//                        a = new Alarma();
//                        a.setAlarm(WebActivity.this, referenciaPago, tipoPago+"", 20000, WebActivity.this);
//                    }

//                    JSONArray json = new JSONArray(res);
//                    String url = json.getString(0);
//                    referenciaPago = json.getString(1);
//                    if (url.startsWith("http")) {
//                        wvPago = (WebView) findViewById(R.id.wvPago);
//                        wvPago.loadUrl(url);
//                        wvPago.setVisibility(View.VISIBLE);
//                        wvPago.setWebViewClient(new WebViewClient());
//                        wvPago.getSettings().setJavaScriptEnabled(true);
//                        Alarma a = new Alarma();
//                        a.setAlarm(WebActivity.this, referenciaPago, "1", 20000, WebActivity.this);
//                    } else if (referenciaPago.startsWith("http")) {
//                        String temp = referenciaPago;
//                        referenciaPago = url;
//                        url = temp;
//                        wvPago = (WebView) findViewById(R.id.wvPago);
//                        wvPago.loadUrl(url);
//                        wvPago.setVisibility(View.VISIBLE);
//                        wvPago.setWebViewClient(new WebViewClient());
//                        wvPago.getSettings().setJavaScriptEnabled(true);
//                        Alarma a = new Alarma();
//                        a.setAlarm(WebActivity.this, referenciaPago, "1", 20000, WebActivity.this);
//                    } else {
//                        Toast.makeText(WebActivity.this, "Error al generar referencia, intenta de nuevo: ", Toast.LENGTH_LONG).show();
//                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //}
        ).execute();
    }

    public void alertClose(){
        if(cambioExitoso){
            finish();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Seguro que deseas abandonar el sitio?");
        builder.setMessage("Es posible que los cambios que implementaste no se puedan guardar.");
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Alarma a=new Alarma();
                //a.cancelAlarm(WebActivity.this);
                //alertToClose.dismiss();
                WebActivity.this.stopRepeatingTask();
                WebActivity.this.finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertToClose.dismiss();
            }
        });
        alertToClose = builder.create();
        alertToClose.show();
    }

    // ============== check status payment ===================
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                new getIsPaySucceded(WebActivity.this, referenciaPago, tipoPago, new SimpleCallBack() {
                    @Override
                    public void run(boolean status, String res) {
                        //                    if (res.equalsIgnoreCase("402")) {
//
//                        String tempRefPago = app_preferences.getString("ref_pago", "null");
//                        if (!referenciaPago.equals(tempRefPago)) {
//                            app_preferences.edit().putString("ref_pago", referenciaPago).apply();
//
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
//                            builder.setTitle("Atención");
//                            builder.setMessage("Pago denegado.");
//                            builder.setCancelable(true);
//                            builder.setPositiveButton("Ok", (dialog, which) -> {
//                                showUrl();
//                                ispagoshown = false;
//                                //stopRepeatingTask();
//                                //finish();
//                            });
//                            builder.setOnDismissListener(dialog -> ispagoshown = false);
//                            runOnUiThread(() -> {
//                                if (!ispagoshown) {
//                                    alertaPago = builder.create();
//                                    ispagoshown = true;
//                                    alertaPago.show();
//                                }
//                            });
//                        }
//                    } else
                        if (res.contains("ESTA REFERENCIA YA FUE UTILIZADA EN OTRA POLIZA") || res.contains("approved")) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
                            builder.setTitle("Gracias");
                            builder.setMessage("Cambio de método de pago exitoso");
                            builder.setCancelable(false);
                            stopRepeatingTask();

                            cambioExitoso = true;
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ispagoshown2 = false;
                                    finish();
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!ispagoshown2) {
                                        alertaPago = builder.create();
                                        ispagoshown2 = true;
                                        alertaPago.show();
                                    }
                                }
                            });
                        }
                    }
                }

                        //}
                ).execute();

            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        alertClose();
        return false;
    }

    @Override
    public void onBackPressed() {
        alertClose();
    }

    //=========================== obtiene liga de pago ==============================================
    public class getReference extends AsyncTask<String, Void, Void> {

        ProgressDialog progress = new ProgressDialog(WebActivity.this);
        String ErrorCode, res;
        SimpleCallBack callBack;
        int idCliente = 0;
        int idPoliza = 0;
        int tipoPago=1;
        public getReference(int idCliente, int idPoliza, int tipoPago, SimpleCallBack cb) {
            this.callBack = cb;
            this.idCliente = idCliente;
            this.idPoliza = idPoliza;
            this.tipoPago=tipoPago;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setTitle("Información");
            progress.setMessage("Solicitando información de pago...");
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progress.dismiss();
            Toast message = Toast.makeText(WebActivity.this, ErrorCode, Toast.LENGTH_LONG);
            message.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            ApiClientCotiza client = new ApiClientCotiza(WebActivity.this);
            try {
                //recupera datos
                //this.res = client.getNewReference("Payment/GetMitNewToken/" + idCliente + "/Policy/" + idPoliza);
                this.res = client.getNewReference("distancePayment", this.idPoliza, this.tipoPago, IinfoClient.getInfoClientObject().getClient().getToken());
            } catch (Exception ex) {
                ErrorCode = ex.getMessage();
                this.cancel(true);
                callBack.run(false, ErrorCode);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            callBack.run(true, res);
        }
    }

    //=========================== checa si el pago fue aprobado ==============================================
    public static class getIsPaySucceded extends AsyncTask<String, Void, Void> {

        String ErrorCode, res;
        String referencia = "";
        int tipoPago=3;
        SimpleCallBack cb;
        Context c;

        public getIsPaySucceded(Context c, String referencia, int tipoPago,SimpleCallBack cb) {
            this.referencia = referencia;
            this.cb = cb;
            this.c=c;
            this.tipoPago=tipoPago;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(String... strings) {

            ApiClientCotiza client = new ApiClientCotiza(c);
            try {
                //recupera datos
                this.res = client.getIsPaySuccededToken("payment/Searchreference/" + referencia + "/"+tipoPago);
                JSONObject j = new JSONObject(res);
                if (res == null || res.contains("AUN NO SE ENCUENTRA") || j.has("Message")) {
                    cb.run(false, null);
                }
            } catch (Exception ex) {
                ErrorCode = ex.getMessage();
                if (ErrorCode == null || ErrorCode.contains("AUN NO SE ENCUENTRA")) {
                    cb.run(false, null);
                }else {
                    cb.run(false, ErrorCode);
                }
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cb.run(false, res);
        }
    }
}