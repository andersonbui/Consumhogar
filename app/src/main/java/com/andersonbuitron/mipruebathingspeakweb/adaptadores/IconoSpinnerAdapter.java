package com.andersonbuitron.mipruebathingspeakweb.adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andersonbuitron.mipruebathingspeakweb.R;

public class IconoSpinnerAdapter extends ArrayAdapter<String>
{
    private Context c;
    private String[] nombres_dispositivos;
    private int[] imagenes;

    public IconoSpinnerAdapter(Context ctx, String[] nombres_dispositivos, int[] imagenes)
    {
        super(ctx, R.layout.item_spinner_icono, nombres_dispositivos);
        this.c = ctx;
        this.nombres_dispositivos = nombres_dispositivos;
        this.imagenes = imagenes;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_spinner_icono, null);
        }

        TextView tvNombre =  (TextView)convertView.findViewById(R.id.tvNombre);
        ImageView icono_disp = (ImageView)convertView.findViewById(R.id.icono_disp);

        tvNombre.setText(nombres_dispositivos[position]);
        icono_disp.setImageResource(imagenes[position]);

        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_spinner_icono, null);
        }

        TextView tvNombre =  (TextView)convertView.findViewById(R.id.tvNombre);
        ImageView icono_disp = (ImageView)convertView.findViewById(R.id.icono_disp);

        tvNombre.setText(nombres_dispositivos[position]);
        icono_disp.setImageResource(imagenes[position]);

        return convertView;
    }
}
