package com.miituo.miituolibrary.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faltenreich.skeletonlayout.Skeleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.miituo.miituolibrary.R;
import com.miituo.miituolibrary.activities.adapters.PromosAdapter;
import com.miituo.miituolibrary.activities.adapters.VehicleRecyclerAdapter;
import com.miituo.miituolibrary.activities.data.IinfoClient;
import com.miituo.miituolibrary.activities.data.InfoClient;
import com.miituo.miituolibrary.activities.threats.GetCuponAsync;
import com.miituo.miituolibrary.activities.threats.GetPoliciesData;
import com.miituo.miituolibrary.activities.utils.CallBack;
import com.miituo.miituolibrary.activities.utils.SimpleCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static com.miituo.miituolibrary.activities.api.ApiClient.ambiente;

public class PrincipalActivity extends AppCompatActivity implements CallBack {

    String telefono;
    SharedPreferences app_preferences;
    public int idpoliza;
    public Timer timer;
    private ViewPager viewPager;
    private PromosAdapter adapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    private VehicleRecyclerAdapter vadapter;
    private SwipeRefreshLayout swipeContainer;

    public String tok_basic, tokencliente;
    private RecyclerView recyclerView;
    public static List<InfoClient> result;
    static AlertDialog alerta;

    public Double kms = 0.0;
    public TextView sinPolizas;
    ImageView imgSinPolizas;
    private ConstraintLayout skeletonView, constraintDataParent;
    private Skeleton skeletonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        app_preferences = getSharedPreferences(getString(R.string.shared_name_prefs), Context.MODE_PRIVATE);

        telefono = getIntent().getStringExtra("telefono");
        if(telefono == null){
            telefono = app_preferences.getString("Celphone","null");
            if(telefono.equals("null")){
                launchAlert("No cuentas con pólizas activas.");
            }
        }else{
            app_preferences.edit().putString("Celphone", telefono).apply();
        }

        int dev = getIntent().getIntExtra("dev", -1);
        if(dev == -1){
            dev = app_preferences.getInt("dev",-1);
            if(dev == -1){
                ambiente = 0;
                app_preferences.edit().putInt("dev", 1).apply();
            }
            else if(dev == 0){
                ambiente = 4;
                app_preferences.edit().putInt("dev", 0).apply();
            }else if(dev == 1){
                ambiente = 0;
                app_preferences.edit().putInt("dev", 1).apply();
            }
        }else if(dev == 0){
            ambiente = 4;
            app_preferences.edit().putInt("dev", 0).apply();
        }else if(dev == 1){
            ambiente = 0;
            app_preferences.edit().putInt("dev", 1).apply();
        }

        skeletonView = findViewById(R.id.skeletonView);
        constraintDataParent = findViewById(R.id.constraintDataParent);
        skeletonLayout = findViewById(R.id.skeletonLayout);
        sinPolizas = (TextView) findViewById(R.id.lbSinPolizas);
        imgSinPolizas = (ImageView) findViewById(R.id.imgSinPolizas);

        result = new ArrayList<>();
        long starttime = app_preferences.getLong("time", 0);
        recyclerView = findViewById(R.id.listviewinfoclient);
        vadapter = new VehicleRecyclerAdapter(getApplicationContext(), result, starttime, PrincipalActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        result = new ArrayList<InfoClient>(){};
        vadapter = new VehicleRecyclerAdapter(getApplicationContext(), result, starttime, PrincipalActivity.this);
        recyclerView.setAdapter(vadapter);

        pageSwitcher();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPolizasData(app_preferences.getString("Celphone", "0"));
            }

        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ImageView imageViewClose = findViewById(R.id.imageViewClose);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app_preferences.edit().putInt("reload",-1).apply();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPolizasData(telefono);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timer != null)
            timer.cancel();
    }

    public void getPolizasData(final String telefono){
        String url = "InfoClientMobil/Celphone/"+telefono;

        constraintDataParent.setVisibility(View.INVISIBLE); // hide data
        skeletonView.setVisibility(View.VISIBLE);   // show skeleton
        skeletonLayout.showSkeleton();

        new GetPoliciesData(url, PrincipalActivity.this, new SimpleCallBack() {
            @Override
            public void run(boolean status, String res) {
                if (!status){
                    String data[] = res.split("@");
                    launchAlert(data[1]);
                }else{
                    //tenemos polizas, recuperamos list y mandamos a sms...
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putString("polizas", res);
                    editor.putString("Celphone", telefono);
                    editor.apply();

                    Gson parseJson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
                    List<InfoClient> InfoList = parseJson.fromJson(res, new TypeToken<List<InfoClient>>() {
                    }.getType());

                    result = InfoList;

                    if (result.size() < 1) {
                        launchAlert("No cuenta con pólizas activas.");
                    }else{
                        String na = result.get(0).getClient().getName();
                        app_preferences.edit().putString("nombre", na).apply();
                        TextView textViewNombre = findViewById(R.id.textViewNombre);
                        textViewNombre.setText(na);

                        removeInvalidPolicies();

                        vadapter.updateReceiptsList(result);
                        vadapter.notifyDataSetChanged();

                        if(result.size() > 0) {
                            tokencliente = result.get(0).getClient().getToken();
                        }else{
                            tokencliente = "";
                        }

                        swipeContainer.setRefreshing(false);
                        obtenerCupon();
                    }
                }

                skeletonLayout.showOriginal();
                skeletonView.setVisibility(View.INVISIBLE); //hide skeleton
                constraintDataParent.setVisibility(View.VISIBLE); // show data

            }
        }).execute();
    }

    private void removeInvalidPolicies() {
        for (int i = (result.size() - 1); i >= 0; i--) {
            if (result.get(i).getPolicies().getReportState() == 4) {
                result.remove(i);
            }
        }
    }

    public void pageSwitcher() {
        timer = new Timer();
        timer.schedule(new RemindTask(), 9000, 9000);
    }

    class RemindTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        if (viewPager.getCurrentItem() == 0) {
                            viewPager.setCurrentItem(1, true);
                        } else {
                            viewPager.setCurrentItem(0, true);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }
    }

//TODO - obtener cupon y mostrarlo en los banners---------------------------------------------------
    public void obtenerCupon(){
        String url = "Cupon/getReferredClientCoupon/"+app_preferences.getString("Celphone", "0");
        GetCuponAsync gp = new GetCuponAsync(PrincipalActivity.this, url, new SimpleCallBack(){

            @Override
            public void run(boolean status, String res) {
                if (status) {

                    try {
                        //Crear los elementos json para obtener los datos del servicio...
                        JSONArray array = new JSONArray(res);
                        JSONObject o = array.getJSONObject(0);
                        JSONObject cupones = o.getJSONObject("Cupones");
                        kms = cupones.getDouble("Kms");

                        InicializarBanners();
                        //Toast.makeText(PrincipalActivity.this, "kilometraje:"+ kms, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        kms = 100.0;
                        InicializarBanners();
                    }
                }else{
                    kms = 100.0;
                    InicializarBanners();
                }
            }
        });
        gp.execute();
    }

    public void InicializarBanners(){

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        String cupon = "";
        if (result != null && result.size() > 0) {
            cupon = result.get(0).getClient().getCupon();
        }
        adapter = new PromosAdapter(this, cupon, kms.intValue());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                TextView tv = (TextView) findViewById(R.id.terms);
                if (position == 1) {
                    tv.setText("*Consulta términos y condiciones.");
                } else {
                    tv.setText("*Aplica un cupón al mes por póliza solo si \n" +
                            "reportaste tu odómetro y pagaste el mes anterior.");
                }
                addBottomDots(position);
                timer.cancel();
                pageSwitcher();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }


            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        dotsLayout.removeAllViews();
        TextView tv = (TextView) findViewById(R.id.terms);
        tv.setText("Aplica un cupón al mes por póliza solo si \n" +
                "reportaste tu odómetro y pagaste el mes anterior.");
        addBottomDots(0);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[2];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");//Html.fromHtml("&#8226;"));
            dots[i].setTextSize(20);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    public void launchAlert(String res){
        AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalActivity.this);
        builder.setTitle("Atención");
        builder.setMessage(res);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrincipalActivity.this.finish();
            }
        });
        AlertDialog alerta = builder.create();
        if(!isFinishing()) {
            alerta.show();
        }
    }

    @Override
    public void runInt(int value) {
        int velocidad = 0;
        if (velocidad >= 5) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalActivity.this);
            builder.setTitle("¡Vas manejando!");
            builder.setMessage("Reporta más tarde…");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Intent i = new Intent(PrincipalActivity.this, SyncActivity.class);
                    //startActivity(i);
                }
            });
            AlertDialog alerta = builder.create();
            alerta.show();
        } else {
            InfoClient item = result.get(value);
            IinfoClient.setInfoClientObject(item);
            IinfoClient.getInfoClientObject().getClient().setCelphone(app_preferences.getString("Celphone", "0"));

            tok_basic = item.getClient().getToken();
            idpoliza = IinfoClient.getInfoClientObject().getPolicies().getId();

            //set token...
            //IinfoClient.getInfoClientObject().getClient().setToken(token);
            Intent i;
            if (!item.getPolicies().isHasVehiclePictures() && !item.getPolicies().isHasOdometerPicture()) {
                i = new Intent(PrincipalActivity.this, VehiclePictures.class);
                app_preferences.edit().putString("odometro", "first").apply();
                startActivity(i);
            } else if (!item.getPolicies().isHasVehiclePictures() && item.getPolicies().isHasOdometerPicture()) {
                i = new Intent(PrincipalActivity.this, VehiclePictures.class);
                app_preferences.edit().putString("odometro", "first").apply();
                app_preferences.edit().putString("solofotos", "1").apply();
                startActivity(i);
            } else if (item.getPolicies().isHasVehiclePictures() && !item.getPolicies().isHasOdometerPicture()) {
                i = new Intent(PrincipalActivity.this, VehicleOdometer.class);
                app_preferences.edit().putString("odometro", "first").apply();
                startActivity(i);
            } else if (item.getPolicies().getReportState() == 13) {
                i = new Intent(PrincipalActivity.this, VehicleOdometer.class);
                app_preferences.edit().putString("odometro", "mensual").apply();
                startActivity(i);
            } else if (item.getPolicies().getReportState() == 14) {
                i = new Intent(PrincipalActivity.this, VehicleOdometer.class);
                app_preferences.edit().putString("odometro", "cancela").apply();
                i.putExtra("isCancelada", true);
                startActivity(i);
            } else if (item.getPolicies().getReportState() == 15) {
                i = new Intent(PrincipalActivity.this, VehicleOdometer.class);
                app_preferences.edit().putString("odometro", "ajuste").apply();
                startActivity(i);
            }
//          else if (item.getPolicies().getReportState() == 21) {
//                app_preferences.edit().putString("solofotos", "1").apply();
//                getfotosFaltantes fotos = new getfotosFaltantes();
//                fotos.execute();
//            }
            else {
                if (item.getPolicies().getState().getId() == 15) {
                    i = new Intent(PrincipalActivity.this, InfoCancelActivity.class);
                } else {
                    i = new Intent(PrincipalActivity.this, DetallesActivity.class);
                    i.putExtra("indexp", value);
                }
                startActivity(i);
            }
        }
    }

    @Override
    public void runInt2(int value) {

        //Clic into row to launch activity
        InfoClient item = result.get(value);
        //static class -- set client in this part
        IinfoClient.setInfoClientObject(item);
        IinfoClient.getInfoClientObject().getClient().setCelphone(app_preferences.getString("Celphone", "0"));

        tok_basic = item.getClient().getToken();
        idpoliza = IinfoClient.getInfoClientObject().getPolicies().getId();
        //getNewQuotation(item.getPolicies().getId());
    }

    //TODO - call Atlas action -------------------------------------------------------------------------
    public void llamarAtlas(final InfoClient v) {

        //startActivity(new Intent(this, MapsActivity.class));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(v.getPolicies().getInsuranceCarrier().getName());
        builder.setMessage("¡Reportar siniestro!");
        builder.setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alerta.dismiss();
                try {
                    String noTel = "8008493917";
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", noTel, null));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alerta.dismiss();
            }
        });
        alerta = builder.create();
        alerta.show();
    }

}