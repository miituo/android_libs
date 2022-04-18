package com.miituo.miituolibrary.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.miituo.miituolibrary.R;
import com.miituo.miituolibrary.activities.api.ApiClient;
import com.miituo.miituolibrary.activities.data.IinfoClient;
import com.miituo.miituolibrary.activities.data.InfoClient;
import com.miituo.miituolibrary.activities.utils.TextWatcherCustom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LastOdometerActivity extends AppCompatActivity {

    public String odometroAnterior;

    public String tipoodometro,tok;
    SharedPreferences app_preferences;

    final String UrlAjuste="Ticket";
    final String UrlAjusteCasi="UpStateCasification";
    final String UrlAjustelast="Policy/UpdatePolicyStatusReport/";

    final String UrlConfirmOdometer="ImageProcessing/ConfirmOdometer";

    private ApiClient api;

    public Boolean response;
    public String respuesta,res;
    final String ApiSendReport="ReportOdometer/";

    public String datoamount = "";
    TextInputLayout odometerconfirm_layout;
    TextInputEditText odometro;

    public int mensualidad = 0;
    public Button aceptar;
    public RelativeLayout loadingGracias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_odometer);

        TextView leyenda = (TextView)findViewById(R.id.textView8);
        TextView res = (TextView)findViewById(R.id.textView41);
        EditText edit = (EditText)findViewById(R.id.editTextConfirmaOdo);

        odometroAnterior = getIntent().getStringExtra("valor");

        app_preferences = getSharedPreferences(getString(R.string.shared_name_prefs), Context.MODE_PRIVATE);
        tipoodometro = app_preferences.getString("odometro","null");

        odometro = findViewById(R.id.editTextConfirmaOdo);
        odometerconfirm_layout = findViewById(R.id.odometerconfirm_layout);
        odometro.addTextChangedListener(new TextWatcherCustom(odometerconfirm_layout));

        //get image and how into imageview here
        SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_name_prefs), Context.MODE_PRIVATE);

        String mCurrentPhotoPath = preferences.getString("nombrefotoodometro", "null");

        String filePath = mCurrentPhotoPath;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bmp = BitmapFactory.decodeFile(filePath,options);

        ImageView vistaodo = findViewById(R.id.imageView18);
        vistaodo.setImageBitmap(bmp);

        api=new ApiClient(this);
        try {
            tok = IinfoClient.getInfoClientObject().getClient().getToken();
        }catch (Exception e){
            try {
                if (!app_preferences.getString("polizas", "null").equals("null")) {
                    Gson parseJson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
                    List<InfoClient> polizas = parseJson.fromJson(app_preferences.getString("polizas", "null"), new TypeToken<List<InfoClient>>() {
                    }.getType());
                    if (polizas.size() > 0) {
                        tok = polizas.get(0).getClient().getToken();
                    } else {
                        Toast.makeText(this, "Sin autorización, intente mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Tenemos un problema para leer la información. Favor de contactar a miituo para mayor información.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }catch (Exception ee){
                Toast.makeText(this, "Tenemos un problema para leer la información. Favor de contactar a miituo para mayor información.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void validar(View v)
    {
        //get odometro
        String odometroText = odometro.getText().toString();

        //odometroAnterior = "1111111";
        //tipoodometro = "mensual";
        //validar que no sea ""
        //validar que coincida con el anterior
        //lanzar alertdialog custom para verificar odometro una vez mas...
        if(odometroText.equals("")){
            odometerconfirm_layout.setHelperText("Captura los kms. que marca el odómetro.");
        } else if(!odometroText.equals(odometroAnterior)){
            odometerconfirm_layout.setHelperText("Los odómetros no coinciden, por favor verifícalos.");
        }else{
            //udpate value into listclient
            IinfoClient.InfoClientObject.getPolicies().setRegOdometer(Integer.parseInt(odometroText));

            //all it's ok...launch alertdialog custom_validation_odometer
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_validation_odometer);
            //dialog.setTitle("Tu odómetro");

            DecimalFormat formatter = new DecimalFormat("#,###,###");
            Double dos = Double.parseDouble(odometroText);
            String yourFormattedString = formatter.format(dos);
            TextView odom = (TextView)dialog.findViewById(R.id.textView44);
            odom.setText(yourFormattedString);

            //buttons actiosn
            Button cancel = (Button)dialog.findViewById(R.id.buttonCancel);
            aceptar = (Button)dialog.findViewById(R.id.buttonOk);
            loadingGracias = dialog.findViewById(R.id.loadingGracias);

            //canecler logic
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //launch vehicle odometet again...
                    Intent i = new Intent(LastOdometerActivity.this, ConfirmActivity.class);
                    //dialog.dismiss();
                    startActivity(i);
                }
            });

            //aceptar logic
            aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Aqui validamos si es first o meontlhy
                    aceptar.setVisibility(View.GONE);
                    loadingGracias.setVisibility(View.VISIBLE);

                    String fotos = app_preferences.getString("solofotos","null");
                    if(fotos.equals("1")){
                        app_preferences.edit().putString("solofotos","0").apply();
                        new SendOdometro().execute();
                    }
                    else if(tipoodometro.equals("first")){
                        new reporteFirst().execute();
                        //dialog.dismiss();
                    }else if(tipoodometro.equals("mensual")){
                        //reporteMensual();
                        new sendReporter().execute();
                        //dialog.dismiss();
                    }else if(tipoodometro.equals("cancela")){
                        new ajusteMensual().execute();    //mismo flujo para cancelacion
                        //dialog.dismiss();
                    }else if(tipoodometro.equals("ajuste")){
                        new ajusteMensual().execute();
                        //dialog.dismiss();
                    }else{
                        //dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }
    }

//solo actualiza y ya*******************************************************************************
    //thread to call api and save furst odometer
    public class SendOdometro extends AsyncTask<Void, Void, Void> {
        String ErrorCode = "";
        boolean response;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //response = api.ConfirmOdometer(5, UrlConfirmOdometer);
                response = api.AjusteOdometerLast(5, UrlAjustelast,IinfoClient.getInfoClientObject().getPolicies().getId(),tok);

            } catch (IOException ex) {
                ErrorCode = ex.getMessage();
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            aceptar.setVisibility(View.VISIBLE);
            loadingGracias.setVisibility(View.INVISIBLE);

            if (!response) {
                //Toast msg = Toast.makeText(getApplicationContext(), "Error inesperado, no se pudo procesar la imagen", Toast.LENGTH_LONG);
                //msg.show();
                AlertDialog.Builder builder = new AlertDialog.Builder(LastOdometerActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Error al subir información. Intente más tarde");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                        startActivity(i);
                    }
                });
                AlertDialog alerta = builder.create();
                alerta.show();
            } else {
                new android.app.AlertDialog.Builder(LastOdometerActivity.this)
                        .setTitle("Gracias.")
                        .setMessage("Tu información ha sido actualizada.")
                        //.setIcon(R.drawable.miituo)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //progresslast.dismiss();
                                //finalizamos....
                                //IinfoClient.getInfoClientObject().getPolicies().setReportState(13);

                                //updateReportState();
                                IinfoClient.getInfoClientObject().getPolicies().setLastOdometer(IinfoClient.getInfoClientObject().getPolicies().getRegOdometer());
                                //IinfoClient.getInfoClientObject().getPolicies().setHasVehiclePictures(true);
                                //IinfoClient.getInfoClientObject().getPolicies().setHasOdometerPicture(true);
                                IinfoClient.getInfoClientObject().getPolicies().setReportState(12);

                                //String rs = UpdateDataBase(modelBase.FeedEntryPoliza.TABLE_NAME,IinfoClient.getInfoClientObject().getPolicies().getNoPolicy());

                                Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                                i.putExtra("actualizar","1");
                                startActivity(i);
                            }
                        })
                        .show();
            }
            //progress.dismiss();
        }

        @Override
        protected void onCancelled() {
            aceptar.setVisibility(View.VISIBLE);
            loadingGracias.setVisibility(View.INVISIBLE);

            if (ErrorCode.equals("1000")) {
                Toast msg = Toast.makeText(getApplicationContext(), ErrorCode, Toast.LENGTH_LONG);
                msg.show();

                super.onCancelled();

            } else {

                Toast msg = Toast.makeText(getApplicationContext(), "Ocurrio un Error:" + ErrorCode, Toast.LENGTH_LONG);
                msg.show();
            }
        }
    };

//ajuste cancela time*****************************************************************************
    public class ajusteMensual extends AsyncTask<Void, Void, Void>{
        //ProgressDialog progress = new ProgressDialog(LastOdometerActivity.this);
        String ErrorCode = "";

        @Override
        protected void onPreExecute() {
    //            progress.setTitle("Registrando odómetro");
    //            progress.setMessage("Procesando información.");
    //            //progress.setIcon(R.drawable.miituo);
    //            progress.setCancelable(false);
    //            progress.setIndeterminate(true);
    //            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //response = api.ConfirmOdometer(5, UrlConfirmOdometer);
                response = api.AjusteOdometerCasification(5, UrlAjusteCasi,LastOdometerActivity.this,tok);
                response = api.AjusteOdometerTicket(5, UrlAjuste,LastOdometerActivity.this,tok);
                response = api.AjusteOdometerLast(5, UrlAjustelast,IinfoClient.getInfoClientObject().getPolicies().getId(),tok);

            } catch (IOException ex) {
                ErrorCode = ex.getMessage();
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            aceptar.setVisibility(View.VISIBLE);
            loadingGracias.setVisibility(View.INVISIBLE);

            if (!response) {
                //Toast msg = Toast.makeText(getApplicationContext(), "Error inesperado, no se pudo procesar la imagen", Toast.LENGTH_LONG);
                //msg.show();
                AlertDialog.Builder builder = new AlertDialog.Builder(LastOdometerActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Error al subir información. Intente más tarde");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                        startActivity(i);
                    }
                });
                AlertDialog alerta = builder.create();
                alerta.show();
            } else {
                new android.app.AlertDialog.Builder(LastOdometerActivity.this)
                        .setTitle("Gracias.")
                        .setMessage("Tu información ha sido actualizada.")
                        //.setIcon(R.drawable.miituo)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //progresslast.dismiss();
                                //finalizamos....
                                //IinfoClient.getInfoClientObject().getPolicies().setReportState(13);

                                //updateReportState();
                                IinfoClient.getInfoClientObject().getPolicies().setLastOdometer(IinfoClient.getInfoClientObject().getPolicies().getRegOdometer());
                                //IinfoClient.getInfoClientObject().getPolicies().setHasVehiclePictures(true);
                                //IinfoClient.getInfoClientObject().getPolicies().setHasOdometerPicture(true);
                                IinfoClient.getInfoClientObject().getPolicies().setReportState(12);

                                //String rs = UpdateDataBase(modelBase.FeedEntryPoliza.TABLE_NAME,IinfoClient.getInfoClientObject().getPolicies().getNoPolicy());

                                Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                                i.putExtra("actualizar","1");
                                startActivity(i);
                            }
                        })
                        .show();
            }
            //progress.dismiss();
        }

        @Override
        protected void onCancelled() {
            aceptar.setVisibility(View.VISIBLE);
            loadingGracias.setVisibility(View.INVISIBLE);

            if (ErrorCode.equals("1000")) {
                Toast msg = Toast.makeText(getApplicationContext(), ErrorCode, Toast.LENGTH_LONG);
                msg.show();

                super.onCancelled();

            } else {

                Toast msg = Toast.makeText(getApplicationContext(), "Ocurrio un Error:" + ErrorCode, Toast.LENGTH_LONG);
                msg.show();
            }
        }
    }

//reporte firt time*****************************************************************************
    public class reporteFirst extends AsyncTask<Void, Void, Void>{
        //ProgressDialog progress = new ProgressDialog(LastOdometerActivity.this);
        String ErrorCode = "";

        @Override
        protected void onPreExecute() {
    //            progress.setTitle("Registrando odómetro");
    //            progress.setMessage("Procesando información.");
    //            //progress.setIcon(R.drawable.miituo);
    //            progress.setCancelable(false);
    //            progress.setIndeterminate(true);
    //            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = api.ConfirmOdometer(5, UrlConfirmOdometer,tok);

            } catch (IOException ex) {
                ErrorCode = ex.getMessage();
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            aceptar.setVisibility(View.VISIBLE);
            loadingGracias.setVisibility(View.INVISIBLE);

            if (!response) {
                //Toast msg = Toast.makeText(getApplicationContext(), "Error inesperado, no se pudo procesar la imagen", Toast.LENGTH_LONG);
                //msg.show();
                AlertDialog.Builder builder = new AlertDialog.Builder(LastOdometerActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Error al subir información. Intente más tarde");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                        i.putExtra("actualizar","1");
                        startActivity(i);
                    }
                });
                AlertDialog alerta = builder.create();
                alerta.show();
            } else {

                new android.app.AlertDialog.Builder(LastOdometerActivity.this)
                        .setTitle("Gracias.")
                        .setMessage("Tu información ha sido actualizada.")
                        //.setIcon(R.drawable.miituo)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //progresslast.dismiss();
                                //finalizamos....
                                //IinfoClient.getInfoClientObject().getPolicies().setReportState(13);

                                //updateReportState();
                                IinfoClient.getInfoClientObject().getPolicies().setLastOdometer(IinfoClient.getInfoClientObject().getPolicies().getRegOdometer());
                                IinfoClient.getInfoClientObject().getPolicies().setHasVehiclePictures(true);
                                IinfoClient.getInfoClientObject().getPolicies().setHasOdometerPicture(true);
                                IinfoClient.getInfoClientObject().getPolicies().setReportState(12);

                                //String rs = UpdateDataBase(modelBase.FeedEntryPoliza.TABLE_NAME,IinfoClient.getInfoClientObject().getPolicies().getNoPolicy());

                                Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                                i.putExtra("actualizar","1");
                                startActivity(i);
                            }
                        })
                        .show();
            }
            //progress.dismiss();
        }

        @Override
        protected void onCancelled() {
            aceptar.setVisibility(View.VISIBLE);
            loadingGracias.setVisibility(View.INVISIBLE);
            if (ErrorCode.equals("1000")) {
                Toast msg = Toast.makeText(getApplicationContext(), ErrorCode, Toast.LENGTH_LONG);
                msg.show();

                super.onCancelled();

            } else {
                Toast msg = Toast.makeText(getApplicationContext(), "Ocurrio un Error:" + ErrorCode, Toast.LENGTH_LONG);
                msg.show();
            }
        }
    }
    //reporte firt time*****************************************************************************
//    public void getNewQuotation(final int mensualidad){
//        //thread to call api and save furst odometer
//        AsyncTask<Void, Void, Void> getQuotation = new AsyncTask<Void, Void, Void>() {
//            ProgressDialog progress = new ProgressDialog(LastOdometerActivity.this);
//            String ErrorCode = "";
//            String resp="";
//            @Override
//            protected void onPreExecute() {
//                progress.setTitle("Consultando información");
//                progress.setMessage("Espere por favor");
//                //progress.setIcon(R.drawable.miituo);
//                progress.setCancelable(false);
//                progress.setIndeterminate(true);
//                progress.show();
//            }
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    resp = api.getQuotation( "Quotation/GetPreQuotationInfo/"+IinfoClient.getInfoClientObject().getPolicies().getId(),LastOdometerActivity.this);
////                    resp = api.getQuotation( "Quotation/GetPreQuotationInfo/165",LastOdometerActivity.this);
//                } catch (Exception ex) {
//                    ErrorCode = ex.getMessage();
//                    this.cancel(true);
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                progress.dismiss();
//                if (resp != null && !resp.equalsIgnoreCase("null")) {
//                    try {
//                        JSONObject jo = new JSONObject(resp);
//                        if (mensualidad == 10) {
//                            Intent i = new Intent(LastOdometerActivity.this, MainActivity.class);
//                            i.putExtra("idPush", "2");
//                            DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy");
//
//                            Calendar c = Calendar.getInstance();
//                            Date fechacadena = IinfoClient.InfoClientObject.getPolicies().getVigencyDate();
//                            c.setTime(fechacadena);
//                            c.add(Calendar.DATE, -1);
//                            fechacadena = c.getTime();
//                            String reportDate = df.format(fechacadena);
//
//                            i.putExtra("tarifa", jo.getString("strNewRate")+"/"+
//                                    jo.getString("PolicyCost").substring(0,jo.getString("PolicyCost").indexOf("."))+"/"+reportDate+
//                                    "/"+IinfoClient.InfoClientObject.getPolicies().getCoverage().getDescription());
//                            startActivity(i);
//                        } else {
//                            Intent i = new Intent(LastOdometerActivity.this, MainActivity.class);
//                            i.putExtra("idPush", "3");
//                            startActivity(i);
//                        }
//                    } catch (Exception e) {
//                        ErrorCode = e.getMessage();
//                        this.cancel(true);
//                    }
//                }
//                else {
//                    Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    i.putExtra("actualizar", "1");
//                    startActivity(i);
//                }
//            }
//            @Override
//            protected void onCancelled() {
////                progress.dismiss();
//                Toast msg = Toast.makeText(getApplicationContext(), "Ocurrio un Error:" + ErrorCode, Toast.LENGTH_LONG);
//                msg.show();
//                Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                i.putExtra("actualizar", "1");
//                startActivity(i);
//            }
//        };
//        getQuotation.execute();
//    }

    //reporte mensaul****************************************************************************
//reporte mensaul****************************************************************************
    public class sendReporter extends AsyncTask<Void, Void, Void>{
        //ProgressDialog progress = new ProgressDialog(LastOdometerActivity.this);
        String ErrorCode = "";

        @Override
        protected void onPreExecute() {
//            progress.setTitle("Odómetro");
//            progress.setMessage("Enviando información...");
//            progress.setIndeterminate(true);
//            progress.setCancelable(false);
//            //progress.setIcon(R.drawable.miituo);
//            progress.show();
        }

        @Override
        protected void onCancelled() {
            aceptar.setVisibility(View.VISIBLE);
            loadingGracias.setVisibility(View.INVISIBLE);

            Toast msg = Toast.makeText(getApplicationContext(), ErrorCode, Toast.LENGTH_LONG);
            msg.show();

            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                IinfoClient.getInfoClientObject().getPolicies().setRegOdometer(Integer.parseInt(odometroAnterior));
                respuesta = api.ConfirmReport(ApiSendReport,tok,false,false);

            } catch (Exception ex) {
                ErrorCode = ex.getMessage();
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            try {
                aceptar.setVisibility(View.VISIBLE);
                loadingGracias.setVisibility(View.INVISIBLE);

                JSONObject object = new JSONObject(respuesta);
                datoamount = object.getString("Amount");
                JSONArray arrayfee = new JSONArray(object.getString("Parameters"));
                JSONArray cupones = object.isNull("Cupons")?new JSONArray():object.getJSONArray("Cupons");

                String actual = odometroAnterior;
                String recorridos="";
                String fee = "";
                String promo = "";
                String feepormktarifa = "";
                String anterior = "";
                String diferencia = "";
                String cobroportarifa = "";
                String ivafee = "";
                String ivatarifa = "";
                String KmsCupon_Vacaciones="";
                String KmsCupon_Cliente="";
                String KmsCupon_Referido="";
                String Cupones="";
                String KmsDescuento="";
                String TopeKms="";
                String parametro_tope_kms="";

                for (int i=0;i<arrayfee.length();i++){
                    JSONObject o=arrayfee.getJSONObject(i);
                    String name=o.getString("Name");
                    if(name.equalsIgnoreCase("Fee")){fee =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("tarifa")){feepormktarifa =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("Tope_Kms")){TopeKms =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("kilometroAct")){actual =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("kilometroReg")){anterior =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("KmCobrado")){diferencia =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("TarifaNeta")){cobroportarifa =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("KmsCupon_Cliente")){KmsCupon_Cliente =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("KmsCupon_Referido")){KmsCupon_Referido =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("FIva")){ivafee =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("IT")){ivatarifa =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("Cupones")){Cupones =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("KmsDescuento")){KmsDescuento =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("promocion")){promo =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("parametro_tope_kms")){parametro_tope_kms =o.getString("Amount");}
                    else if(name.equalsIgnoreCase("KmsCupon_Vacaciones")){KmsCupon_Vacaciones =o.getString("Amount");}
                }

                final Dialog dialog = new Dialog(LastOdometerActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_alert);
                dialog.setCanceledOnTouchOutside(false);

                TextView t47 = dialog.findViewById(R.id.textView47);

                TextView lbCondonados2 = (TextView)dialog.findViewById(R.id.lbCondonados2);
                TextView lbVacaciones2 = (TextView)dialog.findViewById(R.id.lbVacaciones2);
                t47 = dialog.findViewById(R.id.lbReferidos);
                TextView lbReferidos2 = (TextView)dialog.findViewById(R.id.lbReferidos2);
                TextView lbVacaciones=(TextView)dialog.findViewById(R.id.lbVacacionesHelp);
                lbVacaciones.setText("Este mes recorriste más de 1000 km,\npero no te preocupes solo te cobraremos 1000 km");
                TextView lbTerminos = (TextView)dialog.findViewById(R.id.textoTerminos);
                lbTerminos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String urlString="https://www.miituo.com/Terminos/getPDF?Url=C%3A%5CmiituoFTP%5CCondiciones_Generales.pdf";
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("http://docs.google.com/gview?embedded=true&url=" + urlString), "text/html");
                        startActivity(intent);
                    }
                });

                recorridos=""+(Integer.parseInt(actual)-Integer.parseInt(anterior));

                if(!KmsDescuento.equalsIgnoreCase("")){
                    LinearLayout ll=(LinearLayout)dialog.findViewById(R.id.cntCondonados);
                    ll.setVisibility(View.VISIBLE);
                    LinearLayout cntOdos=(LinearLayout)dialog.findViewById(R.id.cntOdometros);
                    cntOdos.setVisibility(View.VISIBLE);
                    int acobrar=Integer.parseInt(recorridos)-Integer.parseInt(KmsDescuento);
                    lbCondonados2.setText("-"+KmsDescuento);
                    diferencia=""+acobrar;
                    if(acobrar<=0){
                        diferencia="0";
                    }
                    //                            else if((cupones.length()>0) && (acobrar>Integer.parseInt(parametro_tope_kms))){
                    //                                acobrar=Integer.parseInt(parametro_tope_kms);
                    //                                diferencia=""+acobrar;
                    //                                lbVacaciones.setVisibility(View.VISIBLE);
                    //                                LinearLayout ll2=(LinearLayout)dialog.findViewById(R.id.cntVacaciones);
                    //                                ll2.setVisibility(View.VISIBLE);
                    //                                lbVacaciones2.setText(diferencia);
                    //                            }
                }
                else if(cupones.length()>0){
                    for (int i=0;i<cupones.length();i++) {
                        JSONObject o = cupones.getJSONObject(i);
                        //if(o.getInt("Type")==3){
                        if(o.getInt("Type")==3){
                            lbVacaciones.setVisibility(View.VISIBLE);
                            LinearLayout ll = dialog.findViewById(R.id.cntVacaciones);
                            ll.setVisibility(View.VISIBLE);
                            diferencia=parametro_tope_kms;
                            lbVacaciones2.setText(diferencia);
                            if(cupones.length()==2){
                                //JSONObject o2 = cupones.getJSONObject(i+1);
                                JSONObject o2 = cupones.getJSONObject(0);
                                if (o.getInt("Type")==5){
                                    o2 = cupones.getJSONObject(0);
                                }else{
                                    o2 = cupones.getJSONObject(1);
                                }
                                int dif=Integer.parseInt(parametro_tope_kms)-o2.getInt("Kms");
                                diferencia=""+dif;
                                LinearLayout ll2 = dialog.findViewById(R.id.cntReferidos);
                                ll2.setVisibility(View.VISIBLE);
                                lbReferidos2.setText("-"+o2.getInt("Kms"));
                                //                                        DecimalFormat formattertarifa = new DecimalFormat("0.0000");
                                //                                        datoamount=""+formattertarifa.format((Double.parseDouble(diferencia)*Double.parseDouble(feepormktarifa)));
                                break;
                            }
                        }
                        if(o.getInt("Type")==5){

                            int kmsdif = Integer.parseInt(diferencia);
                            int dif = 0;
                            if (parametro_tope_kms != "" && kmsdif > 1000) {
                                dif=Integer.parseInt(parametro_tope_kms)-o.getInt("Kms");
                            }else{
                                dif=Integer.parseInt(diferencia)-o.getInt("Kms");
                            }
                            diferencia=""+dif;
                            LinearLayout ll2=(LinearLayout)dialog.findViewById(R.id.cntReferidos);
                            ll2.setVisibility(View.VISIBLE);
                            lbReferidos2.setText("-"+o.getInt("Kms"));
                            //                                        DecimalFormat formattertarifa = new DecimalFormat("0.0000");
                            //                                        datoamount=""+formattertarifa.format((Double.parseDouble(diferencia)*Double.parseDouble(feepormktarifa)));
                            break;
                        }
                        if(o.getInt("Type")==1 || o.getInt("Type")==2){
                            LinearLayout ll=(LinearLayout)dialog.findViewById(R.id.cntReferidos);
                            ll.setVisibility(View.VISIBLE);
                            int acobrar=Integer.parseInt(recorridos)-o.getInt("Kms");
                            lbReferidos2.setText("-"+o.getInt("Kms"));
                            diferencia=""+acobrar;
                            if(acobrar<=0){diferencia="0";datoamount="0.0";}
                            //                                    else{
                            //                                        DecimalFormat formattertarifa = new DecimalFormat("0.0000");
                            //                                        datoamount=""+formattertarifa.format((Double.parseDouble(diferencia)*Double.parseDouble(feepormktarifa)));
                            //                                    }
                            if(cupones.length()==2){
                                JSONObject o2 = cupones.getJSONObject(i+1);
                                int dif=Integer.parseInt(parametro_tope_kms)-o.getInt("Kms");
                                diferencia=""+dif;
                                //                                        diferencia="1100";
                                LinearLayout ll2=(LinearLayout)dialog.findViewById(R.id.cntVacaciones);
                                ll2.setVisibility(View.VISIBLE);
                                lbVacaciones.setVisibility(View.VISIBLE);
                                lbVacaciones2.setText(parametro_tope_kms);
                                //                                        DecimalFormat formattertarifa = new DecimalFormat("0.0000");
                                //                                        datoamount=""+formattertarifa.format((Double.parseDouble(diferencia)*Double.parseDouble(feepormktarifa)));
                                break;
                            }
                        }
                    }
                }

                Button cancel = dialog.findViewById(R.id.button3);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(LastOdometerActivity.this, ConfirmActivity.class);
                        startActivity(i);
                    }
                });
                Button aceptar = dialog.findViewById(R.id.button2);
                //call the other method to confirm odometer...
                aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new sendthelast().execute();
                    }
                });

                DecimalFormat formatter = new DecimalFormat("$ #,###.00");
                DecimalFormat formatterTwo = new DecimalFormat("#,###,###");

                //odometros-----------------------------------------------------------------
                TextView hoy = (TextView)dialog.findViewById(R.id.textView11);

                Double dos = Double.parseDouble(actual);
                String yourFormattedString = formatterTwo.format(dos);
                hoy.setText(yourFormattedString);

                TextView antes = (TextView)dialog.findViewById(R.id.textView14);
                Double antesdos = Double.parseDouble(anterior);
                String yourFormattedStringTwo = formatterTwo.format(antesdos);
                antes.setText(yourFormattedStringTwo);

                TextView difer = (TextView)dialog.findViewById(R.id.lbDiferencia);
                //                        Double antestres = Double.parseDouble(diferencia);
                Double antestres = Double.parseDouble(recorridos);
                String formatedThree = formatterTwo.format(antestres);
                difer.setText(formatedThree);

                //Tarifas-------------------------------------------------------------------
                double s1 = Double.parseDouble(feepormktarifa);
                TextView tarifa = (TextView)dialog.findViewById(R.id.textView21);
                DecimalFormat formattertarifa = new DecimalFormat("$ 0.0000");
                tarifa.setText(formattertarifa.format(s1));

                //str = fee.toString().replaceAll("[^\\d]", "");
                s1 = Double.parseDouble(fee);
                Double s2 = Double.parseDouble(ivafee);
                TextView basepormes = (TextView)dialog.findViewById(R.id.textView19);
                //basepormes.setText(formatter.format(Math.round(s1+s2)));
                basepormes.setText(formatter.format((s1+s2)));

                if(fee.equals("0")){
                    basepormes.setVisibility(View.GONE);
                    TextView t18 = (TextView)dialog.findViewById(R.id.textView18);
                    t18.setVisibility(View.GONE);
                }

                //str = cobroportarifa.toString().replaceAll("[^\\d]", "");
                s1 = Double.parseDouble(cobroportarifa);
                Double s3 = Double.parseDouble(ivatarifa);
                TextView tarifafinal = (TextView)dialog.findViewById(R.id.textView24);
                tarifafinal.setText(diferencia);//formatter.format(s1+s3));

                //str = dato.toString().replaceAll("[^\\d]", "");
                s1 = Double.parseDouble(datoamount);
                //TextView ending = (TextView)dialog.findViewById(R.id.textView27);
                TextView ending2 = (TextView)dialog.findViewById(R.id.textView50);
                //ending.setTypeface(typeface);
                //ending.setText(formatter.format(s1));
                if(datoamount.equalsIgnoreCase("0.0")){
                    ending2.setText("$ 0.00");
                }
                else {
                    ending2.setText(formatter.format(s1));
                }
                dialog.show();
            }catch(Exception e){

                e.printStackTrace();
            }
        }
    }

    public class sendthelast extends AsyncTask<Void, Void, Void> {

        ProgressDialog progress = new ProgressDialog(LastOdometerActivity.this);
        String ErrorCode = "";

        @Override
        protected void onPreExecute() {
            progress.setTitle("Estamos realizando tu reporte.");
            progress.setMessage("Esto puede tardar un momento, espere por favor...");
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            //progress.setIcon(R.drawable.miituo);
            progress.show();
        }

        @Override
        protected void onCancelled() {
            try {
                progress.dismiss();
                //Toast msg = Toast.makeText(getApplicationContext(), ErrorCode, Toast.LENGTH_LONG);
                //msg.show();

                new android.app.AlertDialog.Builder(LastOdometerActivity.this)
                        .setTitle("Atención usuario...")
                        .setMessage(ErrorCode)
                        //.setIcon(R.drawable.miituo)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //progresslast.dismiss();
                                Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                                i.putExtra("actualizar", "1");
                                startActivity(i);
                            }
                        })
                        .show();

                super.onCancelled();

            }catch(Exception e){
                new android.app.AlertDialog.Builder(LastOdometerActivity.this)
                        .setTitle("Atención usuario...")
                        .setMessage("Tuvimos un problema, intente más tarde.")
                        //.setIcon(R.drawable.miituo)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //progresslast.dismiss();
                                Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                                i.putExtra("actualizar", "1");
                                startActivity(i);
                            }
                        })
                        .show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            //before launch alert, we have to send the confirmReport
            try {
                res = api.ConfirmReportLast("ReportOdometer/Confirmreport",tok);

            }catch(Exception e){
                e.printStackTrace();
                ErrorCode = "Al parecer tuvimos un problema, inténtalo más tarde.";
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();

            String title = "Tu reporte se ha realizado";
            String mensajee = "Gracias por ser parte del consumo equitativo.";
            if(datoamount.equals("0.0")){
                title = "Gracias";
                mensajee = "Recibimos tu reporte mensual de odómetro.";
            }else if (IinfoClient.getInfoClientObject().getPolicies().getPaymentType().equals("AMEX")){
                title = "Tu pago esta en proceso.";
                mensajee = "En cuanto quede listo te lo haremos saber.";
            }

            if (res.equals("false") || res.equals("true"))
            {
                new android.app.AlertDialog.Builder(LastOdometerActivity.this)
                        .setTitle(title)
                        .setMessage(mensajee)
                        //.setIcon(R.drawable.miituo)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //progresslast.dismiss();
                                //finalizamos....
                                //IinfoClient.getInfoClientObject().getPolicies().setReportState(13);

                                //updateReportState();
                                IinfoClient.getInfoClientObject().getPolicies().setReportState(12);
                                IinfoClient.getInfoClientObject().getPolicies().setLastOdometer(IinfoClient.getInfoClientObject().getPolicies().getRegOdometer());
//                                    if(IinfoClient.getInfoClientObject().getPolicies().getMensualidad()==10){
//                                        getNewQuotation(10);
//                                    }
//                                    else if(IinfoClient.getInfoClientObject().getPolicies().getMensualidad()==11){
//                                        getNewQuotation(11);
//                                    }
//                                    else {
//                                        Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
//                                        i.putExtra("actualizar", "1");
//                                        startActivity(i);
//                                    }
                                Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                                //i.putExtra("actualizar", "1");
                                startActivity(i);
                            }
                        })
                        .show();
            } else {

                if (res.contains("Por el momento tu pago")) {
                    String mensaje;
                    try {
                        JSONObject mes = new JSONObject(res);
                        mensaje = mes.getString("Message");
                        new android.app.AlertDialog.Builder(LastOdometerActivity.this)
                                .setTitle("Odómetro")
                                .setMessage(mensaje)
                                //.setIcon(R.drawable.miituo)
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //progresslast.dismiss();
                                        Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                                        i.putExtra("actualizar", "1");
                                        startActivity(i);
                                    }
                                })
                                .show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    new android.app.AlertDialog.Builder(LastOdometerActivity.this)
                            .setTitle("Odómetro no reportado")
                            .setMessage("Problema al reportar odómetro, intente más tarde.")
                            //.setIcon(R.drawable.miituo)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //progresslast.dismiss();
                                    Intent i = new Intent(LastOdometerActivity.this, PrincipalActivity.class);
                                    i.putExtra("actualizar", "1");
                                    startActivity(i);
                                }
                            })
                            .show();
                }
            }
        }
    }
}

