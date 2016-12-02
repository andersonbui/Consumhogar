package com.andersonbuitron.mipruebathingspeakweb.datos;

import android.os.AsyncTask;
import android.util.Log;

import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaUrl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by debian on 16/11/16.
 */

public class ClienteRemoto extends AsyncTask<Void, Void, String> {

    TareaUrl tareaUrl;
    private static final String TAG = "UsingThingspeakAPI";

    public ClienteRemoto( TareaUrl tareaUrl) {
        this.tareaUrl = tareaUrl;
    }

    protected void onPreExecute() {

    }

    protected String doInBackground(Void... urls) {
        Log.i(TAG, "entro a doInBackground de clienteRemoto\n\n");
        try {
            URL url = new URL(tareaUrl.getUrl());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally {
                urlConnection.disconnect();
            }
        }catch(ConnectException e){
            //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
            Log.e("Servidor","Servidor no encontrado", e);
        }
        catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
        return "";
    }

    protected void onPostExecute(String resultado) {
        tareaUrl.ejecutar(resultado);
    }
}
