package com.miituo.miituolibrary.activities.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.miituo.miituolibrary.R;
import com.miituo.miituolibrary.activities.api.ApiClient;
import com.miituo.miituolibrary.activities.data.InfoClient;
import com.miituo.miituolibrary.activities.utils.CallBack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VehicleRecyclerAdapter extends
        RecyclerView.Adapter<VehicleRecyclerAdapter.ViewHolder>{

    public VehicleRecyclerAdapter(Context mContext, List<InfoClient> mInfoClientList, long tie, CallBack callBack) {
        this.mContext = mContext;
        this.mInfoClientList = mInfoClientList;
        this.callBack=callBack;
        //tipo = t;
        this.time = tie;
        pathPhotos = new ApiClient(this.mContext).pathPhotos;
    }

    public void updateReceiptsList(List<InfoClient> newlist) {
        final ItemDiffCallback diffCallback = new ItemDiffCallback(this.mInfoClientList, newlist);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.mInfoClientList.clear();
        this.mInfoClientList.addAll(newlist);
        diffResult.dispatchUpdatesTo(this);

        //mInfoClientList.clear();
        //mInfoClientList.addAll(newlist);
        //mInfoClientList = newlist;
        this.notifyDataSetChanged();
    }

    private Context mContext;
    private List<InfoClient> mInfoClientList;
    public long time;
    private CallBack callBack;
    public String noTel="";
    public String pathPhotos;

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.infoclient_item, parent, false);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        InfoClient lugar = mInfoClientList.get(posicion);
        holder.personaliza(lugar, posicion, mContext, callBack, pathPhotos);
    }

    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return mInfoClientList.size();
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView TxtNoPolicy, TxtInfo, TxtMensajeLimite, lbRenovacion1, lbRenovacion2;
        RelativeLayout btnInfo;
        LinearLayout btnRenovacion;
        ImageView btnSiniestro;
        ImageView imagen, img;

        public ViewHolder(View itemView) {
            super(itemView);
            TxtNoPolicy=(TextView)itemView.findViewById(R.id.Vehicle);
            TxtInfo=(TextView)itemView.findViewById(R.id.tvInfo);
             TxtMensajeLimite=(TextView)itemView.findViewById(R.id.mensajelimite);
             lbRenovacion1=(TextView)itemView.findViewById(R.id.lbRenovacion1);
             lbRenovacion2=(TextView)itemView.findViewById(R.id.lbRenovacion2);
             btnInfo=(RelativeLayout) itemView.findViewById(R.id.btnGral);
             btnRenovacion=(LinearLayout) itemView.findViewById(R.id.btnRenovacion);
             btnSiniestro=(ImageView)itemView.findViewById(R.id.btnSiniestro);
             imagen = (ImageView)itemView.findViewById(R.id.profile_image);
             img = (ImageView)itemView.findViewById(R.id.StateImage);
        }

        // Personalizamos un ViewHolder a partir de un lugar
        public void personaliza(final InfoClient mInfoClientList,final int position, Context mContext, final CallBack callBack, String pathPhotos) {
            if(imagen != null){
                //pintaImg(imagen,position);
                pintaImg(imagen, mInfoClientList.getPolicies().getId(), mContext, pathPhotos);
            }
            btnSiniestro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.llamarAtlas(mInfoClientList);
                }
            });
            btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.runInt(position);
                }
            });

            if(mInfoClientList.getPolicies().getMensualidad()==11){
                btnRenovacion.setVisibility(View.VISIBLE);
                lbRenovacion1.setTextColor(Color.RED);
                //lbRenovacion1.setTypeface(tipo,Typeface.BOLD);
                lbRenovacion2.setTextColor(Color.RED);
                //lbRenovacion2.setTypeface(tipo);
                btnRenovacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callBack.runInt2(position);
                    }
                });
            }

            try {
                TxtNoPolicy.setText("Póliza: "+String.valueOf(mInfoClientList.getPolicies().getNoPolicy()));
                //v.setTag(mInfoClientList);
                //update image upon statu pictures
                //ImageView img=(ImageView)v.findViewById(R.id.StateImage);
                TxtInfo.setText("Ver información \n"+mInfoClientList.getPolicies().getVehicle().getSubtype().getDescription());
                TxtMensajeLimite.setVisibility(View.GONE);

                Calendar cOK = Calendar.getInstance();
                Date fechacadenaOK = mInfoClientList.getPolicies().getLimitReportDate();
                cOK.setTime(fechacadenaOK);
                cOK.add(Calendar.DATE, -1);
                fechacadenaOK = cOK.getTime();
                SimpleDateFormat month_dateOK = new SimpleDateFormat("dd/MMM");
                String limite = month_dateOK.format(cOK.getTime());

                fechacadenaOK = mInfoClientList.getPolicies().getLimitReportDate();
                cOK.setTime(fechacadenaOK);
                cOK.add(Calendar.DATE, -4);
                fechacadenaOK = cOK.getTime();
                month_dateOK = new SimpleDateFormat("dd/MMM");
                String desde= month_dateOK.format(cOK.getTime());

                TxtInfo.setText("Próximo reporte:\ndel "+desde.replace(".","")+" al "+limite.replace(".","")+" a las 23:59 hrs");
                TxtMensajeLimite.setText("Ver información");
                TxtMensajeLimite.setVisibility(View.VISIBLE);
                TxtInfo.setTextColor(mContext.getResources().getColor(R.color.colorGray));
                TxtMensajeLimite.setTextColor(mContext.getResources().getColor(R.color.colorGray));

                if(mInfoClientList.getPolicies().getState().getId()==15){
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtInfo.setText("Cancelación en proceso...");
                    TxtMensajeLimite.setVisibility(View.GONE);
                    btnSiniestro.setVisibility(View.GONE);
                }
                else if(mInfoClientList.getPolicies().getReportState() == 24){
                    btnInfo.setBackgroundColor(Color.parseColor("#FFF5F5"));
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtMensajeLimite.setTextColor(Color.RED);
                    if(mInfoClientList.getPolicies().getMensualidad()==0) {
                        TxtInfo.setText("HUBO UN PROBLEMA CON TU PAGO Y TU PÓLIZA NO ESTÁ ACTIVA");
                    }
                    else{
                        TxtInfo.setText("HUBO UN PROBLEMA CON TU PAGO Y TU PÓLIZA ESTA EN RIESGO DE CANCELARSE");
                    }
                    TxtMensajeLimite.setVisibility(View.VISIBLE);
                    TxtMensajeLimite.setText("Comunicate con nosotros para más información");
                }
                else if(mInfoClientList.getPolicies().getReportState() == 23){
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtInfo.setText("Estamos procesando tu pago...\n "+mInfoClientList.getPolicies().getVehicle().getSubtype().getDescription());
                    TxtMensajeLimite.setVisibility(View.GONE);

                }
                else if(mInfoClientList.getPolicies().getReportState() == 21){
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtInfo.setText("Envía nuevamente las fotos de tu\n"+mInfoClientList.getPolicies().getVehicle().getSubtype().getDescription());
                    TxtMensajeLimite.setVisibility(View.GONE);

                }
                else if(mInfoClientList.getPolicies().getReportState() == 15){
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtInfo.setText("Ajuste odómetro\n"+mInfoClientList.getPolicies().getVehicle().getSubtype().getDescription());
                    TxtMensajeLimite.setVisibility(View.GONE);

                }
                else if(mInfoClientList.getPolicies().getReportState() == 14){
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtInfo.setText("Cancelación voluntaria\n"+mInfoClientList.getPolicies().getVehicle().getSubtype().getDescription());
                    TxtMensajeLimite.setVisibility(View.GONE);
                }
                else if(mInfoClientList.getPolicies().getReportState() == 13){
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtInfo.setText("Es hora de reportar tu odómetro "+mInfoClientList.getPolicies().getVehicle().getSubtype().getDescription());
                    TxtMensajeLimite.setVisibility(View.VISIBLE);
                    TxtMensajeLimite.setTextColor(Color.RED);
                    Calendar c = Calendar.getInstance();
                    Date fechacadena = mInfoClientList.getPolicies().getLimitReportDate();
                    c.setTime(fechacadena);
                    c.add(Calendar.DATE, -1);
                    fechacadena = c.getTime();

                    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                    String month_name = month_date.format(c.getTime());
                    String real = getName(month_name);
                    SimpleDateFormat year_data = new SimpleDateFormat("yyyy");
                    String year = year_data.format(c.getTime());
                    SimpleDateFormat hour = new SimpleDateFormat("HH");
                    //String hora = hour.format(c.getTime());
                    String hora = "23";
                    SimpleDateFormat minuts = new SimpleDateFormat("mm");
                    //String minuto = minuts.format(c.getTime());
                    String minuto = "59";

                    //TxtMensajeLimite.setText("Tienes hasta el:\n"+new SimpleDateFormat("dd").format(fechacadena)+" del "+real+" a las "+hora+":"+minuto);
                    TxtMensajeLimite.setText("Tienes hasta el:\n"+new SimpleDateFormat("dd").format(fechacadena)+" de "+real+" a las 23:59 hrs.");

                }
                if(!mInfoClientList.getPolicies().isHasVehiclePictures())
                {
                    //rojo
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtInfo.setText("No has enviado fotos de tu\n"+mInfoClientList.getPolicies().getVehicle().getSubtype().getDescription());
                    TxtMensajeLimite.setVisibility(View.VISIBLE);
                    TxtMensajeLimite.setTextColor(Color.RED);
                    Calendar c = Calendar.getInstance();
                    Date fechacadena = mInfoClientList.getPolicies().getLimitReportDate();
                    c.setTime(fechacadena);
                    //c.add(Calendar.DATE, -1);
                    fechacadena = c.getTime();

                    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                    String month_name = month_date.format(c.getTime());
                    String real = getName(month_name);
                    SimpleDateFormat year_data = new SimpleDateFormat("yyyy");
                    String year = year_data.format(c.getTime());
                    SimpleDateFormat hour = new SimpleDateFormat("HH");
                    String hora = hour.format(c.getTime());
                    SimpleDateFormat minuts = new SimpleDateFormat("mm");
                    String minuto = minuts.format(c.getTime());

                    TxtMensajeLimite.setText("Tienes hasta el:\n"+new SimpleDateFormat("dd").format(fechacadena)+" de "+real+" a las "+hora+":"+minuto+" hrs.");
                }
                else if(mInfoClientList.getPolicies().isHasVehiclePictures() && !mInfoClientList.getPolicies().isHasOdometerPicture())
                {
                    //rojo
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtInfo.setText("No has enviado foto de tu odómetro "+mInfoClientList.getPolicies().getVehicle().getSubtype().getDescription());
                    TxtMensajeLimite.setVisibility(View.VISIBLE);
                    TxtMensajeLimite.setTextColor(Color.RED);

                    Calendar c = Calendar.getInstance();
                    Date fechacadena = mInfoClientList.getPolicies().getLimitReportDate();
                    c.setTime(fechacadena);
                    //c.add(Calendar.DATE, -1);
                    fechacadena = c.getTime();

                    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                    String month_name = month_date.format(c.getTime());
                    String real = getName(month_name);
                    SimpleDateFormat year_data = new SimpleDateFormat("yyyy");
                    String year = year_data.format(c.getTime());
                    SimpleDateFormat hour = new SimpleDateFormat("HH");
                    String hora = hour.format(c.getTime());
                    SimpleDateFormat minuts = new SimpleDateFormat("mm");
                    String minuto = minuts.format(c.getTime());

                    //TxtMensajeLimite.setText("Tienes hasta el:\n"+new SimpleDateFormat("dd").format(fechacadena)+" del "+real+" a las 23:59 hrs.");
                    TxtMensajeLimite.setText("Tienes hasta el:\n"+new SimpleDateFormat("dd").format(fechacadena)+" de "+real+" a las "+hora+":"+minuto+" hrs.");
                }
                else if(!mInfoClientList.getPolicies().isHasVehiclePictures() && mInfoClientList.getPolicies().isHasOdometerPicture())
                {
                    //rojo
                    img.setImageResource(R.drawable.reedmiituo);
                    TxtInfo.setTextColor(Color.RED);
                    TxtInfo.setText("Vuelve a enviar las fotos de tu\n"+mInfoClientList.getPolicies().getVehicle().getSubtype().getDescription());
                    TxtMensajeLimite.setVisibility(View.VISIBLE);
                    TxtMensajeLimite.setTextColor(Color.RED);

                    Calendar c = Calendar.getInstance();
                    Date fechacadena = mInfoClientList.getPolicies().getLimitReportDate();
                    c.setTime(fechacadena);
                    c.add(Calendar.DATE, -1);
                    fechacadena = c.getTime();

                    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                    String month_name = month_date.format(c.getTime());
                    String real = getName(month_name);
                    SimpleDateFormat year_data = new SimpleDateFormat("yyyy");
                    String year = year_data.format(c.getTime());
                    SimpleDateFormat hour = new SimpleDateFormat("HH");
                    String hora = hour.format(c.getTime());
                    SimpleDateFormat minuts = new SimpleDateFormat("mm");
                    String minuto = minuts.format(c.getTime());

                    TxtMensajeLimite.setText("Tienes hasta el:\n"+new SimpleDateFormat("dd").format(fechacadena)+" de "+real+" a las 23:59 hrs");
                }
            }
            catch (Exception ex)
            {
                Log.e("tag_miituo", "en el error de model adapter");
                ex.printStackTrace();
            }
            //return v;
        }

        private void pintaImg(final ImageView iv, int id, Context mContext, String pathPhotos) {
            //ruta del archivo...https://filesdev.miituo.com/21/FROM_VEHICLE.png

            Glide.with(mContext).asBitmap().
                    load(pathPhotos + id + "/FROM_VEHICLE.png")
                    //.into(iv);
                    .listener(new RequestListener<Bitmap>() {
                                  @Override
                                  public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                      iv.setImageResource(R.drawable.vista_auto_2);
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                      iv.setImageBitmap(bitmap);// .setImage(ImageSource.bitmap(bitmap));
                                      return false;
                                  }
                              }
                    ).submit();

        /*String filePathString = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+File.separator+"frontal"+time+mInfoClientList.get(position).getPolicies().getNoPolicy()+".png";
        String filePathStringfirst = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+File.separator+"frontal_"+mInfoClientList.get(position).getPolicies().getNoPolicy()+".png";
        File f = new File(filePathString);
        File f2 = new File(filePathStringfirst);
        //File image = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+File.separator+username+"_"+polizaFolio+tag+".png");
        if(f.exists()) {
            Glide.with(mContext)
                    .load(filePathString)
                    .into(iv);
        }else if(f2.exists()) {
            Glide.with(mContext)
                    .load(filePathStringfirst)
                    .into(iv);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.foto)
                    .into(iv);
        }*/
        }

        public String getName(String nombre){

            if(nombre.equals("ene") || nombre.equals("Jan") || nombre.equals("jan") || nombre.equals("Ene")){
                return "ene";
            }else if(nombre.equals("feb") || nombre.equals("Feb")){
                return "feb";
            }else if (nombre.equals("mar") || nombre.equals("Mar")){
                return "mar";
            }else if (nombre.equals("abr") || nombre.equals("apr") || nombre.equals("Apr")){
                return "abr";
            }else if (nombre.equals("may") || nombre.equals("May")){
                return "may";
            }else if (nombre.equals("jun") || nombre.equals("Jun")){
                return "jun";
            }else if (nombre.equals("jul") || nombre.equals("Jul")){
                return "jul";
            }else if (nombre.equals("ago") || nombre.equals("aug") || nombre.equals("Aug") || nombre.equals("Ago")){
                return "ago";
            }else if (nombre.equals("sep") || nombre.equals("Sep")){
                return "09";
            }else if (nombre.equals("oct") || nombre.equals("Oct")){
                return "oct";
            }else if (nombre.equals("nov") || nombre.equals("Nov")){
                return "nov";
            }else if (nombre.equals("dic") || nombre.equals("dec") || nombre.equals("Dic") || nombre.equals("Dec")){
                return "dic";
            }else{
                return nombre;
            }
        }
    }
}
