package com.andersonbuitron.mipruebathingspeakweb.gestores;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.R;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaList;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaUrl;
import com.andersonbuitron.mipruebathingspeakweb.database.BDDispositivo;
import com.andersonbuitron.mipruebathingspeakweb.datos.ClienteRemoto;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_API_KEY;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_API_KEY_STRING;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_CHANNELS;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_END;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_FIELD;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_FIELDS;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_LAST;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_START;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_SUM;
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

    public static String FORMATO_FECHA = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static int VALUE_FIELD_NUMBER = 2;
    public static int SWITCH_FIELD = 1;

    public static String SWITCH_FIELD_VALUE_PRENDIDO = "1";
    public static String SWITCH_FIELD_VALUE_APAGADO = "0";

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

    public void obtenerDispositivosEn(ArrayAdapter adapter) {
        this.adapter = adapter;
        realizarSolicitudGET(new TareaUrl() {
            @Override
            public void ejecutar(String resultado) {
                ArrayList<Dispositivo> canales = (ArrayList<Dispositivo>) parseArrayCanal(resultado);
                canales = fitrarCanales(canales);
                actualizarAdaptador(canales);
                if (canales.isEmpty()) {
                    Toast.makeText(context, "No hay mas Dispositivos disponibles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public String getUrl() {
                String url = THINGSPEAK_URL + THINGSPEAK_CHANNELS + URL_JSON + URL_CHAR_QUESTION +
                        THINGSPEAK_API_KEY_STRING + THINGSPEAK_API_KEY + "";
                return url;
            }
        });
    }

    public void recuperarDispositivosBaseDatos(ArrayAdapter adapter) {
        this.adapter = adapter;
        BDDispositivo bdcanal = BDDispositivo.getInstance(context);
        ArrayList listaDispos = bdcanal.leerCanales();
        actualizarAdaptador(listaDispos);
    }

    public void eliminarDispositivo(String idDispositivo, ArrayAdapter adapter) {
        this.adapter = adapter;
        BDDispositivo bdcanal = BDDispositivo.getInstance(context);
        bdcanal.deleteCanal(idDispositivo);
        ArrayList listaDispos = bdcanal.leerCanales();
        actualizarAdaptador(listaDispos);
    }

    public List<Dispositivo> recuperarDispositivosBaseDatos() {
        BDDispositivo bdcanal = BDDispositivo.getInstance(context);
        ArrayList listaDispos = bdcanal.leerCanales();
        return listaDispos;
    }

    private void realizarSolicitudGET(final TareaUrl tareaUrl) {

        //opcion con volley
        //requestConVolley(tareaUrl);

        //opcion con asinctask
        new ClienteRemoto(tareaUrl).execute();
    }
    private void requestConVolley(final TareaUrl tareaUrl){
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


/*
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
    }*/

    /**
     * Encargados de enviar un dato a un field de un canal con su respectivo api_key
     *
     * @param api_key
     * @param field
     * @param valor
     * @param view
     */
    public void enviarDatoThingSpeak(String api_key, int field, String valor, CompoundButton view) {
        //opcion con volley
        EnviarValorFieldCanal tarea = new EnviarValorFieldCanal(api_key, field, valor, view);
        realizarSolicitudGET(tarea);
    }

    public void guardarConsumo(int consumoKWh,Activity actividad) {
        SharedPreferences sharedPref = actividad.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(actividad.getString(R.string.saved_valor_consumo), consumoKWh);
        editor.commit();
    }

    public int leerfConsumo(Activity actividad) {
        int valorConsumo = 0;
        try {
            String PREFS_NAME = "MyPrefsFile";
            // Restore preferences
            SharedPreferences settings = actividad.getSharedPreferences(PREFS_NAME, 0);
            valorConsumo = settings.getInt("consumo", 0);
/*
            SharedPreferences sharedPref = actividad.getPreferences(Context.MODE_PRIVATE);
            valorConsumo = sharedPref.getInt(actividad.getString(R.string.saved_valor_consumo), 0);*/
        }catch (Exception e){
            Log.i("leerConsumo","No se encontro el consumo");
        }

        return valorConsumo;
    }

    private class EnviarValorFieldCanal implements TareaUrl {

        String api_key;
        int field;
        String valor;
        CompoundButton compoundButton;

        public EnviarValorFieldCanal(String api_key, int field, String valor, CompoundButton compoundButton) {
            this.api_key = api_key;
            this.field = field;
            this.valor = valor;
            this.compoundButton = compoundButton;
            this.compoundButton.setEnabled(false);
        }

        @Override
        public void ejecutar(String resultado) {
            //resultado = "[{\"id\":175991,\"name\":\"SocketLatitudLongitud\",\"description\":\"Socket175991\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-10-27T17:22:18Z\",\"elevation\":\"\",\"last_entry_id\":5,\"ranking\":70,\"metadata\":\"ninguno\",\"tags\":[{\"id\":13553,\"name\":\"socket\"},{\"id\":14072,\"name\":\"proyectoiot\"}],\"api_keys\":[{\"api_key\":\"LNIG6BFA4TF38M7Q\",\"write_flag\":true},{\"api_key\":\"MI5UJJBT6FD5BCIY\",\"write_flag\":false}]},{\"id\":181453,\"name\":\"Socket181453\",\"description\":\"Dispositivo01\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T15:06:37Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"LVNMQI6UKASFV7LA\",\"write_flag\":true},{\"api_key\":\"M6FWUOM2S917N0M0\",\"write_flag\":false}]},{\"id\":181528,\"name\":\"Socket181528\",\"description\":\"asd\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T18:33:28Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"IMJ8ZRC3GG4TR9GD\",\"write_flag\":true},{\"api_key\":\"5G8HEPJIID11ZS31\",\"write_flag\":false}]},{\"id\":181978,\"name\":\"CanalesRegistrados\",\"description\":\"CanalesRegistrados\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-11T19:27:02Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"WTGLQE8YBPYRKDUK\",\"write_flag\":true},{\"api_key\":\"02V97LOG7MRJD56K\",\"write_flag\":false}]}]";
            int result = Integer.parseInt(resultado.trim());
            //Toast.makeText(context, "Response is: " + resultado, Toast.LENGTH_SHORT).show();
            if (result == 0 && intentos > 0) {
                intentos--;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                enviarDatoThingSpeak(api_key, field, valor, compoundButton);
            } else {
                if (result == 0) {
                    if (compoundButton.isChecked()) {
                        compoundButton.setChecked(false);
                    } else {
                        compoundButton.setChecked(true);
                    }
                }
                compoundButton.setEnabled(true);
                Toast.makeText(context, "resultado[" + resultado + "] - intentos[" + intentos + "]", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public String getUrl() {
            String url = THINGSPEAK_URL + THINGSPEAK_UPDATE + URL_CHAR_QUESTION +
                    THINGSPEAK_API_KEY_STRING + api_key + URL_CHAR_AMEPERSAND +
                    THINGSPEAK_FIELD + field + "=" + valor;
            Log.i("url", url);
            return url;
        }
    }

    /**
     * Encargados de solicitar el ultimo idcanal entregado a un field de un canal con su respectivo api_key
     *
     * @param api_key
     * @param field
     * @param idCanal
     */
    public void solicitarUltimoValorDeField(String api_key, int field, String idCanal) {
        //opcion con volley
        SolicitudUltimoValorDeField tarea = new SolicitudUltimoValorDeField(api_key, field, idCanal);
        realizarSolicitudGET(tarea);
    }

    private class SolicitudUltimoValorDeField implements TareaUrl {

        String api_key;
        int field;
        String idcanal;

        public SolicitudUltimoValorDeField(String api_key, int field, String idcanal) {
            this.api_key = api_key;
            this.field = field;
            this.idcanal = idcanal;
        }

        @Override
        public void ejecutar(String resultado) {
            //resultado = "[{\"id\":175991,\"name\":\"SocketLatitudLongitud\",\"description\":\"Socket175991\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-10-27T17:22:18Z\",\"elevation\":\"\",\"last_entry_id\":5,\"ranking\":70,\"metadata\":\"ninguno\",\"tags\":[{\"id\":13553,\"name\":\"socket\"},{\"id\":14072,\"name\":\"proyectoiot\"}],\"api_keys\":[{\"api_key\":\"LNIG6BFA4TF38M7Q\",\"write_flag\":true},{\"api_key\":\"MI5UJJBT6FD5BCIY\",\"write_flag\":false}]},{\"id\":181453,\"name\":\"Socket181453\",\"description\":\"Dispositivo01\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T15:06:37Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"LVNMQI6UKASFV7LA\",\"write_flag\":true},{\"api_key\":\"M6FWUOM2S917N0M0\",\"write_flag\":false}]},{\"id\":181528,\"name\":\"Socket181528\",\"description\":\"asd\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T18:33:28Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"IMJ8ZRC3GG4TR9GD\",\"write_flag\":true},{\"api_key\":\"5G8HEPJIID11ZS31\",\"write_flag\":false}]},{\"id\":181978,\"name\":\"CanalesRegistrados\",\"description\":\"CanalesRegistrados\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-11T19:27:02Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"WTGLQE8YBPYRKDUK\",\"write_flag\":true},{\"api_key\":\"02V97LOG7MRJD56K\",\"write_flag\":false}]}]";
            Toast.makeText(context, "Ultimo valor del field[" + field + "] is: " + resultado, Toast.LENGTH_SHORT).show();
        }

        @Override
        public String getUrl() {
            String url = THINGSPEAK_URL + THINGSPEAK_CHANNELS + "/" + idcanal + "/" + THINGSPEAK_FIELDS + "/" + field + "/" + THINGSPEAK_LAST +
                    URL_JSON;
            Log.i("url", url);
            return url;
        }
    }

    /**
     * Encargados de solicitar el ultimo idcanal entregado a un field de un canal con su respectivo api_key
     *
     * @param api_key
     * @param field
     * @param idCanal
     */

    public String solicitarValoresDeField(
            String api_key,
            int field,
            String idCanal,
            Date finicial,
            Date ffinal,
            String escalaEnMin,
            TareaList callback) {
        //opcion con volley
        SolicitudValoresDeField tarea = new SolicitudValoresDeField(api_key, field, idCanal, finicial, ffinal, escalaEnMin, callback);
        realizarSolicitudGET(tarea);
        return tarea.getUrl();
    }

    protected List<FeedField> parseListaFeedField(String respuestaJson, int field) {
        if (respuestaJson == null) {
            Toast.makeText(context, "Error al recuperar sockets disponibles", Toast.LENGTH_SHORT).show();
            return null;
        }
        ArrayList<FeedField> feedFieldList = new ArrayList<>();
        try {
            JSONObject principal = (JSONObject) new JSONTokener(respuestaJson).nextValue();
            JSONArray array = principal.getJSONArray("feeds");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                DateFormat df = new SimpleDateFormat(FORMATO_FECHA);
                Date fecha = df.parse(obj.getString("created_at"));
                Double valor = obj.optDouble(THINGSPEAK_FIELD + field);
                FeedField unFF = new FeedField(fecha, valor);
                feedFieldList.add(unFF);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return feedFieldList;
    }

    private class SolicitudValoresDeField implements TareaUrl {

        String api_key;
        int field;
        String idcanal;
        Date finicial;
        Date ffinal;
        String escalaEnMin;
        ArrayList<FeedField> listaValores;
        TareaList unaTareaList;

        public SolicitudValoresDeField(
                String api_key,
                int field,
                String idcanal,
                Date finicial,
                Date ffinal,
                String escalaEnMin,
                TareaList tareaCallback) {
            this.escalaEnMin = escalaEnMin;
            this.api_key = api_key;
            this.field = field;
            this.idcanal = idcanal;
            this.finicial = finicial;
            this.ffinal = ffinal;
            unaTareaList = tareaCallback;
            listaValores = (ArrayList) tareaCallback.getList();
        }

        @Override
        public void ejecutar(String resultado) {
            //resultado = "[{\"id\":175991,\"name\":\"SocketLatitudLongitud\",\"description\":\"Socket175991\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-10-27T17:22:18Z\",\"elevation\":\"\",\"last_entry_id\":5,\"ranking\":70,\"metadata\":\"ninguno\",\"tags\":[{\"id\":13553,\"name\":\"socket\"},{\"id\":14072,\"name\":\"proyectoiot\"}],\"api_keys\":[{\"api_key\":\"LNIG6BFA4TF38M7Q\",\"write_flag\":true},{\"api_key\":\"MI5UJJBT6FD5BCIY\",\"write_flag\":false}]},{\"id\":181453,\"name\":\"Socket181453\",\"description\":\"Dispositivo01\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T15:06:37Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"LVNMQI6UKASFV7LA\",\"write_flag\":true},{\"api_key\":\"M6FWUOM2S917N0M0\",\"write_flag\":false}]},{\"id\":181528,\"name\":\"Socket181528\",\"description\":\"asd\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-10T18:33:28Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"IMJ8ZRC3GG4TR9GD\",\"write_flag\":true},{\"api_key\":\"5G8HEPJIID11ZS31\",\"write_flag\":false}]},{\"id\":181978,\"name\":\"CanalesRegistrados\",\"description\":\"CanalesRegistrados\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"created_at\":\"2016-11-11T19:27:02Z\",\"elevation\":\"\",\"last_entry_id\":null,\"ranking\":50,\"metadata\":\"\",\"tags\":[],\"api_keys\":[{\"api_key\":\"WTGLQE8YBPYRKDUK\",\"write_flag\":true},{\"api_key\":\"02V97LOG7MRJD56K\",\"write_flag\":false}]}]";
            Toast.makeText(context, "resultado: " + resultado + "-duracion[]", Toast.LENGTH_LONG).show();
            listaValores = (ArrayList<FeedField>) parseListaFeedField(resultado, field);
            unaTareaList.ejecutar(listaValores);
            //Toast.makeText(context, "LISTA: " + listaValores, Toast.LENGTH_LONG).show();
        }

        @Override
        public String getUrl() {
            //ejemplo
            //https://thingspeak.com/channels/175991/fields/1.json?sum=60&start=2016-11-20T15:00:00&end=2016-11-20T19:00:00

            DateFormat df = new SimpleDateFormat(FORMATO_FECHA);
            String url = THINGSPEAK_URL + THINGSPEAK_CHANNELS + "/" + idcanal + "/" + THINGSPEAK_FIELDS + "/" + field +
                    URL_JSON + URL_CHAR_QUESTION + THINGSPEAK_SUM + escalaEnMin + URL_CHAR_AMEPERSAND +
                    THINGSPEAK_START + df.format(finicial) + URL_CHAR_AMEPERSAND +
                    THINGSPEAK_END + df.format(ffinal);
            Log.i("url", url);
            return url;
        }
        /*
        public String getUrlG() {
            //ejemplo
            //https://thingspeak.com/channels/175991/charts/1.json?sum=60&start=2016-11-20T15:00:00&end=2016-11-20T19:00:00

            DateFormat df = new SimpleDateFormat(FORMATO_FECHA);
            String url = THINGSPEAK_URL + THINGSPEAK_CHANNELS + "/" + idcanal + "/" + "charts" + "/" + field +
                    URL_CHAR_QUESTION + THINGSPEAK_SUM + escalaEnMin + URL_CHAR_AMEPERSAND +
                    THINGSPEAK_START + df.format(finicial) + URL_CHAR_AMEPERSAND +
                    THINGSPEAK_END + df.format(ffinal) +
                    "&title=Salida%20valor%20field2&xaxis=Tiempo&yaxis=valor%20concumo%20[kwts]&color=blue&type=column&width=600&height=400&yaxismax=&dynamic=true";
            Log.i("url", url);
            return url;
        }*/
    }

    private ArrayList<Dispositivo> fitrarCanales(ArrayList<Dispositivo> canales) {

        BDDispositivo bdDispositivo = BDDispositivo.getInstance(context);
        List<Dispositivo> list_canales = bdDispositivo.leerCanales();
        canales.removeAll(list_canales);

        return canales;
    }


}
