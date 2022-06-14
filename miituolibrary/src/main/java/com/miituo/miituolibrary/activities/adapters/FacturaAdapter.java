package com.miituo.miituolibrary.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.miituo.miituolibrary.R;
import com.miituo.miituolibrary.activities.data.FacturasResponse;
import com.miituo.miituolibrary.activities.utils.FacturaCallback;

import java.text.DecimalFormat;
import java.util.List;

public class FacturaAdapter extends RecyclerView.Adapter<FacturaAdapter.ViewHolder>{

    public List<FacturasResponse> items;
    public Context mContext;
    public FacturaCallback callback;

    public FacturaAdapter(Context mContext, List<FacturasResponse> items, FacturaCallback callback){
        this.callback = callback;
        this.mContext = mContext;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_factura, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FacturasResponse item = items.get(position);
        holder.bind(mContext, item, this.callback, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView titleFactura, send_text;
        public ProgressBar sending_factura;
        public ConstraintLayout constraintLayoutButtonSend;

        public ViewHolder(View itemView){
            super(itemView);

            titleFactura = itemView.findViewById(R.id.titleFactura);
            sending_factura = itemView.findViewById(R.id.sending_factura);
            send_text = itemView.findViewById(R.id.send_text);
            constraintLayoutButtonSend = itemView.findViewById(R.id.constraintLayoutButtonSend);
        }

        public void bind(Context ctx, final FacturasResponse item, final FacturaCallback callback, final int pos) {

            DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
            titleFactura.setText(item.getPaymentyype()+" - $"+formatter.format(item.getAmount()));

            if(item.getSending()){
                sending_factura.setVisibility(View.VISIBLE);
                send_text.setVisibility(View.GONE);
            }else{
                sending_factura.setVisibility(View.GONE);
                send_text.setVisibility(View.VISIBLE);
            }
            constraintLayoutButtonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.call(item, pos);
                }
            });
        }
    }
}