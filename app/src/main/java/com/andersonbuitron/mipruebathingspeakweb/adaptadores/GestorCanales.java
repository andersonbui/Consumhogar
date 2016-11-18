package com.andersonbuitron.mipruebathingspeakweb.adaptadores;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.datos.ClienteRemoto;
import com.andersonbuitron.mipruebathingspeakweb.datos.Tarea;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Canal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debian on 10/11/16.
 */

public class GestorCanales {
    private static GestorCanales gestorCanales = new GestorCanales();

    Context context;
    ArrayAdapter adapter;

    String API_KEY = "CCX5C0TQRBUBVFR8";
    private static final String THINGSPEAK_API_KEY = "CCX5C0TQRBUBVFR8";
    private static final String THINGSPEAK_API_KEY_STRING = "api_key";

    private static final String THINGSPEAK_CHANNEL_URL = "https://api.thingspeak.com/channels";
    private static final String THINGSPEAK_JSON = ".json?";

    public static GestorCanales getInstance(){
        return gestorCanales;
    }

    protected List<Canal> obtenerCanales(String response) {
        if(response == null) {
            Toast.makeText(context, "Error al recuperar sockets disponibles", Toast.LENGTH_SHORT).show();
            return null;
        }
        ArrayList<Canal> sockets = new ArrayList<>();
        try {
            JSONArray array = (JSONArray) new JSONTokener(response).nextValue() ;
            for (int i= 0 ; i < array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                sockets.add(leerCanal(obj));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sockets;
    }

    public Canal leerCanal(JSONObject objeto) {

        String id = null;
        String nombre = null;
        try {
            id = objeto.getString("id");
            nombre = objeto.getString("name");
        } catch (JSONException e) {
            Toast.makeText(context, "objeto JSON no valido", Toast.LENGTH_SHORT).show();
        }
        Canal canal =  new Canal(id,nombre);
        return canal;
    }

    public void recuperarCanales(ArrayAdapter adapter) {
        this.adapter = adapter;
        new ClienteRemoto(new ObtenerMisCanales()).execute();
    }

    class ObtenerMisCanales implements Tarea{

        @Override
        public void ejecutar(String resultado) {
            String response = "[{\"id\":175991,\"name\":\"SocketLatitudLongitud\",\"description\":\"Socket175991\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-10-27T17:22:18Z\",\"elevation\":\"\",\"last_entry_id\":5,\"ranking\":70,\"metadata\":\"ninguno\",\"tags\":[{\"id\":13553,\"name\":\"socket\"},{\"id\":14072,\"name\":\"proyectoiot\"}],\"api_keys\":[{\"api_key\":\"LNIG6BFA4TF38M7Q\",\"write_flag\":true},{\"api_key\":\"MI5UJJBT6FD5BCIY\",\"write_flag\":false}]},{\"id\":181453,\"name\":\"Socket181453\",\"description\":\"Dispositivo01\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T15:06:37Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"LVNMQI6UKASFV7LA\",\"write_flag\":true},{\"api_key\":\"M6FWUOM2S917N0M0\",\"write_flag\":false}]},{\"id\":181528,\"name\":\"Socket181528\",\"description\":\"asd\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T18:33:28Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"IMJ8ZRC3GG4TR9GD\",\"write_flag\":true},{\"api_key\":\"5G8HEPJIID11ZS31\",\"write_flag\":false}]},{\"id\":181978,\"name\":\"CanalesRegistrados\",\"description\":\"CanalesRegistrados\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-11T19:27:02Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"WTGLQE8YBPYRKDUK\",\"write_flag\":true},{\"api_key\":\"02V97LOG7MRJD56K\",\"write_flag\":false}]}]";

            //response = resultado;
            ArrayList<Canal> canales;
            canales = (ArrayList<Canal>) obtenerCanales(response);
            ArrayList lista_canales = (ArrayList) ((CanalesAdapter)adapter).getLista_canales();
            lista_canales.clear();
            lista_canales.addAll(canales);
            adapter.notifyDataSetChanged();
        }

        @Override
        public String getUrl() {
            String url = THINGSPEAK_CHANNEL_URL + THINGSPEAK_JSON +
                    THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "";
            return url;
        }
    }

}
