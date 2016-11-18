package com.andersonbuitron.mipruebathingspeakweb.datos;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by debian on 16/11/16.
 */

public class ClienteRemoto extends AsyncTask<Void, Void, String> {

    Tarea tarea;
    private static final String TAG = "UsingThingspeakAPI";

    public ClienteRemoto( Tarea tarea) {
        this.tarea = tarea;
    }

    protected void onPreExecute() {
    }

    protected String doInBackground(Void... urls) {
        Log.i(TAG, "entro a doInBackground de clienteRemoto\n\n");
        try {
            URL url = new URL(tarea.getUrl());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String resultado) {
        tarea.ejecutar(resultado);
    }
}
