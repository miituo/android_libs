package com.miituo.miituolibrary.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.miituo.miituolibrary.R;
import com.miituo.miituolibrary.activities.adapters.PagerAdapter;
import com.miituo.miituolibrary.activities.api.ApiClient;
import com.miituo.miituolibrary.activities.data.IinfoClient;
import com.miituo.miituolibrary.activities.data.InfoClient;
import com.miituo.miituolibrary.activities.threats.GetPDFSync;
import com.miituo.miituolibrary.activities.utils.SimpleCallBack;

import java.io.File;
import java.util.List;

public class DetallesActivity extends AppCompatActivity {
    ViewPager viewPager;
    PagerAdapter adapter;
    public static File pdf;
    public static AlertDialog alertaPago;
    public String pathPhotos = new ApiClient(this).pathPhotos;

    private static final int PERMISSION_CODE = 1000;
    private int indexp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_detalles);
        final View root = findViewById(android.R.id.content); // <-- content view
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        SharedPreferences app_preferences= getSharedPreferences(getString(R.string.shared_name_prefs), Context.MODE_PRIVATE);
        long starttime = app_preferences.getLong("time",0);

        indexp = getIntent().getIntExtra("indexp", -1);
        if(indexp != -1) {
            String res = app_preferences.getString("polizas","");
            Gson parseJson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
            List<InfoClient> InfoList = parseJson.fromJson(res, new TypeToken<List<InfoClient>>() {
            }.getType());

            Log.e("data", res);
            IinfoClient.setInfoClientObject(InfoList.get(indexp));

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            tabLayout.addTab(tabLayout.newTab().setText("").setIcon(R.drawable.docs));
            //tabLayout.addTab(tabLayout.newTab().setText("").setIcon(R.drawable.historial));
            tabLayout.addTab(tabLayout.newTab().setText("").setIcon(R.drawable.timer));
            //tabLayout.addTab(tabLayout.newTab().setText("").setIcon(R.drawable.pago));
            tabLayout.addTab(tabLayout.newTab().setText("").setIcon(R.drawable.phoneblack));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setSelectedTabIndicatorColor(Color.rgb(55, 55, 55));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    //change icon
                    if (tab.getPosition() == 0) {
                        tab.setIcon(R.drawable.docs);
                    } else if (tab.getPosition() == 1) {
                        tab.setIcon(R.drawable.timer);
                    } else if (tab.getPosition() == 2) {
                        tab.setIcon(R.drawable.phoneblack);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    //change icon
                    if (tab.getPosition() == 0) {
                        tab.setIcon(R.drawable.docs);
                    } else if (tab.getPosition() == 1) {
                        tab.setIcon(R.drawable.timer);
                    } else if (tab.getPosition() == 2) {
                        tab.setIcon(R.drawable.phoneblack);
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            init();
            viewPager = (ViewPager) findViewById(R.id.pager);
            adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), this, starttime, viewPager);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        }
    }

    public void init(){
        try {
            TextView lbDescargar = findViewById(R.id.textView37);
            TextView poliza = findViewById(R.id.textViewpolizadetail);
            //poliza.setTypeface(PagerAdapter.tipo);
            poliza.setText(IinfoClient.InfoClientObject.getPolicies().getNoPolicy());
            lbDescargar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (
                                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                                        || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                        ) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
                        } else {
                            callPDF();
                        }
                    } else {
                        callPDF();
                    }
                }
            });

            ImageView fotocarro = (ImageView) findViewById(R.id.imageView5);
            if (fotocarro != null) {
                //pintaImg(imagen,position);
                pintaImg(fotocarro, IinfoClient.InfoClientObject.getPolicies().getId());
            }
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Tuvimos un problema para recuperar tu información.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                callPDF();
            } else {
                Toast.makeText(this, "No se puede descargar el archivo. Acceso denegado.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void callPDF(){
        new GetPDFSync(DetallesActivity.this,true, IinfoClient.InfoClientObject.getPolicies().getId(),IinfoClient.InfoClientObject.getPolicies().getNoPolicy(), new SimpleCallBack() {
            @Override
            public void run(boolean status, String res) {
                if(!status){
//            Toast.makeText(c,"descarga fallida",Toast.LENGTH_LONG).show();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DetallesActivity.this);
                    builder.setTitle("Descarga Fallida");
                    builder.setMessage("Por favor intentalo más tarde.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertaPago.dismiss();
                        }
                    });
                    alertaPago = builder.create();
                    alertaPago.show();
                }else{
                    // Toast.makeText(c,"descarga exitosa",Toast.LENGTH_LONG).show();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DetallesActivity.this);
                    builder.setTitle("Descarga completa");
                    builder.setMessage("Ahora tu póliza se encuentra en tus descargas.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertaPago.dismiss();
//                    c.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                            startActivity(new Intent(getApplicationContext(), PDFViewer.class).putExtra("isPoliza",true));
                        }
                    });
                    alertaPago = builder.create();
                    alertaPago.show();
                }
            }
        }).execute();
    }

    private void pintaImg(final ImageView iv, int id) {
        //ruta del archivo...https://filesdev.miituo.com/21/FROM_VEHICLE.png

        Glide.with(this).asBitmap().
                load(pathPhotos + id + "/FROM_VEHICLE.png")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.foto)
                .into(iv);
    }
}
