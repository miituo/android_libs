package com.miituo.miituolibrary.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.miituo.miituolibrary.R;
import com.miituo.miituolibrary.activities.adapters.FacturaAdapter;
import com.miituo.miituolibrary.activities.data.FacturasResponse;
import com.miituo.miituolibrary.activities.threats.GetFacturasDataAsync;
import com.miituo.miituolibrary.activities.utils.FacturaCallback;
import com.miituo.miituolibrary.activities.utils.SimpleCallBack;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class FacturasActivity extends AppCompatActivity implements FacturaCallback{

    private FacturaAdapter adapter;
    private Context ctx;
    private String token;

    private ProgressBar progress_facturas;
    private RecyclerView list_facturas;
    private CardView cntNoFacturas;

    private ImageView backfacturwas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_facturas);
        final View root = findViewById(android.R.id.content); // <-- content view
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        ctx = this;

        progress_facturas = findViewById(R.id.progress_facturas);
        list_facturas = findViewById(R.id.list_facturas);
        cntNoFacturas = findViewById(R.id.cntNoFacturas);
        backfacturwas = findViewById(R.id.backfacturwas);

        backfacturwas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int idpol = getIntent().getExtras().getInt("idpolicy");
        token = getIntent().getExtras().getString("token");

        list_facturas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FacturaAdapter(this, Collections.<FacturasResponse>emptyList(), FacturasActivity.this);

        list_facturas.setAdapter(adapter);

        String url = "Bill/GetBilledInfo/"+idpol;
        new GetFacturasDataAsync(url, this, new SimpleCallBack() {
            @Override
            public void run(boolean status, String res) {
                if(status){
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<FacturasResponse>>() {
                        }.getType();
                        List<FacturasResponse> resp = gson.fromJson(res, listType);

                        if(resp.size() == 0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress_facturas.setVisibility(View.GONE);
                                    cntNoFacturas.setVisibility( View.VISIBLE);
                                }
                            });
                        }else {
                            for (int i = 0; i < resp.size(); i++) {
                                resp.get(i).setSending(false);
                            }
                            adapter.items = resp;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress_facturas.setVisibility(View.GONE);
                                    list_facturas.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress_facturas.setVisibility(View.GONE);
                                cntNoFacturas.setVisibility( View.VISIBLE);
                            }
                        });
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress_facturas.setVisibility(View.GONE);
                            cntNoFacturas.setVisibility( View.VISIBLE);
                        }
                    });
                }
            }
        }, token ,0).execute();
    }

    @Override
    public void call(final FacturasResponse response, final int pos) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                response.setSending(true);
                adapter.notifyItemChanged(pos);
                sendFactura(response, pos);
            }
        });
    }

    public void sendFactura(final FacturasResponse item, final int pos){
        String url = "Bill/GenerateBill/"+item.getPolicyID()+"/"+item.getMonthBill();
        new GetFacturasDataAsync(url, this, new SimpleCallBack() {
            @Override
            public void run(final boolean status, String res) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item.setSending(false);
                        adapter.notifyItemChanged(pos);

                        AlertDialog.Builder builder = new AlertDialog.Builder(FacturasActivity.this);
                        builder.setTitle((status) ? "Listo" : "Atención");
                        builder.setMessage((status) ? "Tu factura ha sido enviada a tu correo electrónico." : "Recuerda que, después de realizar tu pago, el proceso de facturación puede tardar hasta 15 días.");
                        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alerta = builder.create();
                        alerta.show();
                    }
                });
            }
        }, token, 1).execute();
    }
}