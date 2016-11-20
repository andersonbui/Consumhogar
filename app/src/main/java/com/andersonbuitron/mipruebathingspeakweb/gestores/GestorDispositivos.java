package com.andersonbuitron.mipruebathingspeakweb.gestores;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaUrl;
import com.andersonbuitron.mipruebathingspeakweb.database.BDDispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_API_KEY;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_API_KEY_STRING;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_CHANNELS;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_FIELD;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_UPDATE;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_URL;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.URL_CHAR_AMEPERSAND;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.URL_CHAR_QUESTION;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.URL_JSON;

/**
 * parser y solicitud de canales por medio de una url
 */

public class GestorDispositivos {
    private static GestorDispositivos gestorDispositivos = new GestorDispositivos();

    Context context;
    ArrayAdapter adapter;
    int intentos;

    public static GestorDispositivos getInstance(Context context) {
        gestorDispositivos.context = context;
        gestorDispositivos.intentos = 20;
        return gestorDispositivos;
    }

    protected List<Dispositivo> parseArrayCanal(String respuestaJson) {
        if (respuestaJson == null) {
            Toast.makeText(context, "Error al recuperar sockets disponibles", Toast.LENGTH_SHORT).show();
            return null;
        }
        ArrayList<Dispositivo> sockets = new ArrayList<>();
        try {
            JSONArray array = (JSONArray) new JSONTokener(respuestaJson).nextValue();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                sockets.add(parseCanal(obj));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sockets;
    }

    /**
     * transforma el objeto JSONObject a su respectivo objeto Dispositivo
     *
     * @param objeto
     * @return
     */
    private Dispositivo parseCanal(JSONObject objeto) {

        String id = "";
        String nombre = "";
        String api_key_write = "";
        try {
            id = objeto.getString("id");
            nombre = objeto.getString("name");
            JSONArray array_api_keys = objeto.getJSONArray("api_keys");
            //Toast.makeText(context, "objeto json: "+array_api_keys.toString(), Toast.LENGTH_LONG).show();
            for (int i = 0; i < array_api_keys.length(); i++) {
                if (array_api_keys.getJSONObject(i).getBoolean("write_flag")) {
                    api_key_write = array_api_keys.getJSONObject(i).getString("api_key");
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "objeto JSON no valido", Toast.LENGTH_SHORT).show();
        }
        Dispositivo dispositivo = new Dispositivo(id, nombre, api_key_write);
        return dispositivo;
    }


    public void realizarSolicitudGET(ArrayAdapter adapter) {
        this.adapter = adapter;
        //opcion con volley
        realizarSolicitudGET(new ObtenerMisCanales());
        //opcion con asictask
        //new ClienteRemoto(new ObtenerMisCanales()).execute();
    }

    public void recuperarDispositivosBaseDatos(ArrayAdapter adapter) {
        this.adapter = adapter;
        BDDispositivo bdcanal = BDDispositivo.getInstance(context);
        ArrayList listaDispos = bdcanal.leerCanales();
        actualizarAdaptador(listaDispos);
    }

    public List<Dispositivo> recuperarDispositivosBaseDatos() {
        BDDispositivo bdcanal = BDDispositivo.getInstance(context);
        ArrayList listaDispos = bdcanal.leerCanales();
        return listaDispos;
    }

    private void realizarSolicitudGET(final TareaUrl tareaUrl) {

        String url = tareaUrl.getUrl();
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tareaUrl.ejecutar(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error en la solicitud http", Toast.LENGTH_SHORT).show();
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void actualizarAdaptador(ArrayList<Dispositivo> canales) {

        adapter.clear();
        adapter.addAll(canales);

    }

    class ObtenerMisCanales implements TareaUrl {

        @Override
        public void ejecutar(String resultado) {
            //resultado = "[{\"id\":175991,\"name\":\"SocketLatitudLongitud\",\"description\":\"Socket175991\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-10-27T17:22:18Z\",\"elevation\":\"\",\"last_entry_id\":5,\"ranking\":70,\"metadata\":\"ninguno\",\"tags\":[{\"id\":13553,\"name\":\"socket\"},{\"id\":14072,\"name\":\"proyectoiot\"}],\"api_keys\":[{\"api_key\":\"LNIG6BFA4TF38M7Q\",\"write_flag\":true},{\"api_key\":\"MI5UJJBT6FD5BCIY\",\"write_flag\":false}]},{\"id\":181453,\"name\":\"Socket181453\",\"description\":\"Dispositivo01\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T15:06:37Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"LVNMQI6UKASFV7LA\",\"write_flag\":true},{\"api_key\":\"M6FWUOM2S917N0M0\",\"write_flag\":false}]},{\"id\":181528,\"name\":\"Socket181528\",\"description\":\"asd\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T18:33:28Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"IMJ8ZRC3GG4TR9GD\",\"write_flag\":true},{\"api_key\":\"5G8HEPJIID11ZS31\",\"write_flag\":false}]},{\"id\":181978,\"name\":\"CanalesRegistrados\",\"description\":\"CanalesRegistrados\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-11T19:27:02Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"WTGLQE8YBPYRKDUK\",\"write_flag\":true},{\"api_key\":\"02V97LOG7MRJD56K\",\"write_flag\":false}]}]";

            ArrayList<Dispositivo> canales = (ArrayList<Dispositivo>) parseArrayCanal(resultado);
            canales = fitrarCanales(canales);
            actualizarAdaptador(canales);
            if(canales.isEmpty()){
                Toast.makeText(context, "No hay mas Dispositivos disponibles", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(context, "Response is: " + resultado, Toast.LENGTH_SHORT).show();
        }

        @Override
        public String getUrl() {
            String url = THINGSPEAK_URL + THINGSPEAK_CHANNELS + URL_JSON + URL_CHAR_QUESTION +
                    THINGSPEAK_API_KEY_STRING + THINGSPEAK_API_KEY + "";
            return url;
        }
    }

    class ObtenerEntradasCanal implements TareaUrl {

        @Override
        public void ejecutar(String resultado) {
            //resultado = "[{\"id\":175991,\"name\":\"SocketLatitudLongitud\",\"description\":\"Socket175991\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-10-27T17:22:18Z\",\"elevation\":\"\",\"last_entry_id\":5,\"ranking\":70,\"metadata\":\"ninguno\",\"tags\":[{\"id\":13553,\"name\":\"socket\"},{\"id\":14072,\"name\":\"proyectoiot\"}],\"api_keys\":[{\"api_key\":\"LNIG6BFA4TF38M7Q\",\"write_flag\":true},{\"api_key\":\"MI5UJJBT6FD5BCIY\",\"write_flag\":false}]},{\"id\":181453,\"name\":\"Socket181453\",\"description\":\"Dispositivo01\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T15:06:37Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"LVNMQI6UKASFV7LA\",\"write_flag\":true},{\"api_key\":\"M6FWUOM2S917N0M0\",\"write_flag\":false}]},{\"id\":181528,\"name\":\"Socket181528\",\"description\":\"asd\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T18:33:28Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"IMJ8ZRC3GG4TR9GD\",\"write_flag\":true},{\"api_key\":\"5G8HEPJIID11ZS31\",\"write_flag\":false}]},{\"id\":181978,\"name\":\"CanalesRegistrados\",\"description\":\"CanalesRegistrados\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-11T19:27:02Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"WTGLQE8YBPYRKDUK\",\"write_flag\":true},{\"api_key\":\"02V97LOG7MRJD56K\",\"write_flag\":false}]}]";

            ArrayList<Dispositivo> canales = (ArrayList<Dispositivo>) parseArrayCanal(resultado);
            canales = fitrarCanales(canales);
            actualizarAdaptador(canales);
            if(canales.isEmpty()){
                Toast.makeText(context, "No hay mas Dispositivos disponibles", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(context, "Response is: " + resultado, Toast.LENGTH_SHORT).show();
        }

        @Override
        public String getUrl() {
            String url = THINGSPEAK_URL + THINGSPEAK_CHANNELS + URL_JSON + URL_CHAR_QUESTION +
                    THINGSPEAK_API_KEY_STRING + THINGSPEAK_API_KEY + "";
            return url;
        }
    }

    public void enviarDatoThingSpeak(String api_key,int field, String valor,CompoundButton view) {
        //opcion con volley

        EnviarValorFieldCanal tarea = new EnviarValorFieldCanal(api_key,field,valor,view);
        realizarSolicitudGET(tarea);
    }

    class EnviarValorFieldCanal implements TareaUrl {

        String api_key;
        int field;
        String valor;
        CompoundButton compoundButton;
        public EnviarValorFieldCanal(String api_key, int field, String valor,CompoundButton compoundButton) {
            this.api_key = api_key;
            this.field = field;
            this.valor = valor;
            this.compoundButton = compoundButton;
            this.compoundButton.setEnabled(false);
        }

        @Override
        public void ejecutar(String resultado) {
            //resultado = "[{\"id\":175991,\"name\":\"SocketLatitudLongitud\",\"description\":\"Socket175991\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-10-27T17:22:18Z\",\"elevation\":\"\",\"last_entry_id\":5,\"ranking\":70,\"metadata\":\"ninguno\",\"tags\":[{\"id\":13553,\"name\":\"socket\"},{\"id\":14072,\"name\":\"proyectoiot\"}],\"api_keys\":[{\"api_key\":\"LNIG6BFA4TF38M7Q\",\"write_flag\":true},{\"api_key\":\"MI5UJJBT6FD5BCIY\",\"write_flag\":false}]},{\"id\":181453,\"name\":\"Socket181453\",\"description\":\"Dispositivo01\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T15:06:37Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"LVNMQI6UKASFV7LA\",\"write_flag\":true},{\"api_key\":\"M6FWUOM2S917N0M0\",\"write_flag\":false}]},{\"id\":181528,\"name\":\"Socket181528\",\"description\":\"asd\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T18:33:28Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"IMJ8ZRC3GG4TR9GD\",\"write_flag\":true},{\"api_key\":\"5G8HEPJIID11ZS31\",\"write_flag\":false}]},{\"id\":181978,\"name\":\"CanalesRegistrados\",\"description\":\"CanalesRegistrados\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-11T19:27:02Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"WTGLQE8YBPYRKDUK\",\"write_flag\":true},{\"api_key\":\"02V97LOG7MRJD56K\",\"write_flag\":false}]}]";
                int result = Integer.parseInt(resultado);
            //Toast.makeText(context, "Response is: " + resultado, Toast.LENGTH_SHORT).show();
            if(result == 0 && intentos > 0){
                intentos--;
                enviarDatoThingSpeak( api_key, field, valor, compoundButton);
            }else{
                if(result == 0){
                    if(compoundButton.isChecked()){
                        compoundButton.setChecked(false);
                    }else{
                        compoundButton.setChecked(true);
                    }
                }
                compoundButton.setEnabled(true);
                Toast.makeText(context, "resultado["+resultado+"] - intentos["+intentos+"]", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public String getUrl() {
            String url = THINGSPEAK_URL + THINGSPEAK_UPDATE +URL_CHAR_QUESTION+
                    THINGSPEAK_API_KEY_STRING + api_key +URL_CHAR_AMEPERSAND+
                    THINGSPEAK_FIELD+field+"="+valor
                    ;
            Log.i("url",url);
            return url;
        }
    }

    private ArrayList<Dispositivo> fitrarCanales(ArrayList<Dispositivo> canales) {

        BDDispositivo bdDispositivo = BDDispositivo.getInstance(context);
        List<Dispositivo> list_canales = bdDispositivo.leerCanales();
        canales.removeAll(list_canales);

        return canales;
    }


}
