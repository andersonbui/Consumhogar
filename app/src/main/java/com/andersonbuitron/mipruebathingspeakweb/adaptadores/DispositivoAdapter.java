package com.andersonbuitron.mipruebathingspeakweb.adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

import java.util.List;

/**
 * Created by debian on 10/11/16.
 */

public class DispositivoAdapter extends ArrayAdapter<Dispositivo>{

    Context context;
    List<Dispositivo> lista_canales;

    public DispositivoAdapter(Context context, List<Dispositivo> objects) {
        super(context, 0, objects); //resource  = 0
        int resource = 0;
        this.context = context;
        lista_canales = objects;
    }

    public List<Dispositivo> getLista_canales() {
        return lista_canales;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //obtener inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;

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
            holder.power = (Switch) convertView.findViewById(R.id.sw_reg_power);

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
        Switch power;
    }
}
