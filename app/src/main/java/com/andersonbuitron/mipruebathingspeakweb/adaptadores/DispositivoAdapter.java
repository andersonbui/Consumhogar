package com.andersonbuitron.mipruebathingspeakweb.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.activities.ItemDetailActivity;
import com.andersonbuitron.mipruebathingspeakweb.fragments.ItemDetailFragment;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

import java.util.List;

/**
 * Created by debian on 10/11/16.
 */

public class DispositivoAdapter extends ArrayAdapter<Dispositivo>{

    Context context;
    List<Dispositivo> lista_canales;

    public List<Dispositivo> getLista_canales() {
        return lista_canales;
    }
    ViewHolder holder;

    public DispositivoAdapter(Context context, List<Dispositivo> objects) {
        super(context, 0, objects); //resource  = 0
        int resource = 0;
        this.context = context;
        lista_canales = objects;
    }
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //obtener inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        //Existe el view actual?
        if(null == convertView){
            //si no existe, entonces inflarlo con el image_list_view.xml
            //ligar layout al adaptador
            convertView = inflater.inflate(R.layout.template_list_item_dispositivos,
                    parent,
                    false);
            holder = new ViewHolder();
            holder.nombre = (TextView) convertView.findViewById(R.id.tv_reg_nombre);
            holder.consumo = (TextView) convertView.findViewById(R.id.tv_reg_consumo);
            holder.power = (CompoundButton) convertView.findViewById(R.id.sw_reg_power);

            //comportamiento del switch power
            holder.power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)  {
                    String valor = "";
                    if(isChecked){

                        Toast.makeText(context, "Dispositivo prendido", Toast.LENGTH_SHORT).show();
                        valor = "1";
                    }else {
                        valor = "0";
                        Toast.makeText(context, "Dispositivo apagado", Toast.LENGTH_SHORT).show();
                    }

                    GestorDispositivos.
                            getInstance(context).
                            enviarDatoThingSpeak(getItem(position).getApi_key_write(),2,valor,holder.power);
                }
            });
            
            /*
            //comportamiento del switch power
            holder.power.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CompoundButton buttonView = (CompoundButton)view;
                    String valor = "";
                    if(buttonView.isChecked()){

                        Toast.makeText(context, "Dispositivo prendido", Toast.LENGTH_SHORT).show();
                        valor = "1";
                    }else {
                        valor = "0";
                        Toast.makeText(context, "Dispositivo apagado", Toast.LENGTH_SHORT).show();
                    }

                    GestorDispositivos.
                            getInstance(context).
                            enviarDatoThingSpeak(getItem(position).getApi_key_write(),2,valor,buttonView);
                }

            });*/

            convertView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_DISPOSITIVO, getItem(position));

                    context.startActivity(intent);
                    Toast.makeText(getContext(), "Dispositivo ["+position+"] seleccionado: "+getItem(position).toString(), Toast.LENGTH_SHORT).show();
                }
            } );

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //con holder
        //Lead actual
        Dispositivo dispositivo = getItem(position);
        //setup
        holder.nombre.setText(dispositivo.getNombre());
        // CALCULAR CONSUMO - TODO
        holder.consumo.setText("1500kw-$1200");

        return convertView;
    }

    static class ViewHolder {
        TextView nombre;
        TextView consumo;
        CompoundButton power;
    }
}
