package com.andersonbuitron.mipruebathingspeakweb.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.activities.DetalleDispositivoActivity;
import com.andersonbuitron.mipruebathingspeakweb.gestores.GestorDispositivos;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;

import java.util.List;

import static com.andersonbuitron.mipruebathingspeakweb.activities.DetalleDispositivoActivity.ARG_ITEM_DISPOSITIVO;
import static com.andersonbuitron.mipruebathingspeakweb.activities.DetalleDispositivoActivity.actualizarCompoundButton;

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
            //rev
            holder.icono_reg = (ImageView) convertView.findViewById(R.id.iv_reg_icono);
            //revend
            holder.nombre = (TextView) convertView.findViewById(R.id.tv_reg_nombre);
            holder.consumo = (TextView) convertView.findViewById(R.id.tv_reg_consumo);
            holder.power = (CompoundButton) convertView.findViewById(R.id.sw_reg_power);


            //comportamiento del switch power
            holder.power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)  {
                    notificarCambiodeSwitchApagado(isChecked,context,holder.power,getItem(position).getApi_key_write());
                }
            });
            
            /*
            //comportamiento del switch power
            holder.power.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CompoundButton buttonView = (CompoundButton)view;
                    notificarCambiodeSwitchApagado(isChecked,context,holder.power,getItem(position).getApi_key_write());
                }

            });*/

            convertView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetalleDispositivoActivity.class);
                    intent.putExtra(ARG_ITEM_DISPOSITIVO, getItem(position));

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
        holder.consumo.setText("0.1060kw-$47.7");
        //ultimo datos de field 2 para
        actualizarCompoundButton(holder.power, dispositivo.getId(), GestorDispositivos.SWITCH_FIELD, context);

        //rev
        //Set icon image
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(dispositivo.getIcono(), "drawable", context.getPackageName());
        holder.icono_reg.setImageResource(resourceId);
        //revend
        return convertView;
    }

    public static void notificarCambiodeSwitchApagado(boolean isChecked,Context context,CompoundButton checkCButton,String api_keyDispositivo){
        String valor = "";
        String mensaje = "";

        if(isChecked){
            mensaje = "prendido";
            valor = GestorDispositivos.SWITCH_FIELD_VALUE_PRENDIDO;
        }else {
            mensaje = "apagado";
            valor = GestorDispositivos.SWITCH_FIELD_VALUE_APAGADO;
        }
        Toast.makeText(context, "Dispositivo "+mensaje, Toast.LENGTH_SHORT).show();
        GestorDispositivos.
                getInstance(context).
                enviarDatoThingSpeak(api_keyDispositivo,GestorDispositivos.SWITCH_FIELD,valor,checkCButton);
    }

    static class ViewHolder {
        //rev
        ImageView icono_reg;
        TextView nombre;
        TextView consumo;
        CompoundButton power;
    }
}
