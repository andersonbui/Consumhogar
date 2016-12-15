package com.andersonbuitron.mipruebathingspeakweb.gestores;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by debian on 7/12/16.
 */

public class GestorLlaveValor {

    private Context context;
    private static GestorLlaveValor gestorLV =  new GestorLlaveValor();
    private static String PREFERENCIA_CONSUMO = "preferencia_consumo";

    public static GestorLlaveValor getInstance(Context context) {
        gestorLV.context= context;
        return gestorLV;
    }

    public int obtenerIntValor(String llave)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA_CONSUMO, Context.MODE_PRIVATE);
        int valor = prefs.getInt(llave, 0);
        return valor;
    }

    public boolean guardarValor(int valor, String llave)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA_CONSUMO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(llave, valor);
        editor.commit();
        return true;
    }

    public String obtenerStringValor(String llave)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA_CONSUMO, Context.MODE_PRIVATE);
        String valor = prefs.getString(llave, null);
        return valor;
    }

    public boolean guardarValor(String valor, String llave)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA_CONSUMO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(llave, valor);
        editor.commit();
        return true;
    }


    public boolean obtenerBooleanValor(String llave)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA_CONSUMO, Context.MODE_PRIVATE);
        boolean valor = prefs.getBoolean(llave, false);
        return valor;
    }

    public boolean guardarValor(boolean valor, String llave)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA_CONSUMO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(llave, valor);
        editor.commit();
        return true;
    }

    public float obtenerFloatValor(String llave)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA_CONSUMO, Context.MODE_PRIVATE);
        float valor = prefs.getFloat(llave, Float.NaN);
        return valor;
    }

    public boolean guardarValor(float valor, String llave)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA_CONSUMO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(llave, valor);
        editor.commit();
        return true;
    }


}
