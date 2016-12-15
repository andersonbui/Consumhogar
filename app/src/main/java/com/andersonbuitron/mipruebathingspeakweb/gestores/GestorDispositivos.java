package com.andersonbuitron.mipruebathingspeakweb.gestores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaList;
import com.andersonbuitron.mipruebathingspeakweb.callbacks.TareaString;
import com.andersonbuitron.mipruebathingspeakweb.database.BDDispositivo;
import com.andersonbuitron.mipruebathingspeakweb.datos.ClienteHttpVolley;
import com.andersonbuitron.mipruebathingspeakweb.modelos.Dispositivo;
import com.andersonbuitron.mipruebathingspeakweb.modelos.FeedField;
import com.andersonbuitron.mipruebathingspeakweb.servicios.ServicioNotificacion;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_TIMEZONE;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_UPDATE;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.THINGSPEAK_URL;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.URL_CHAR_AMEPERSAND;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.URL_CHAR_QUESTION;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.URL_JSON;
import static com.andersonbuitron.mipruebathingspeakweb.extras.UrlEndPoints.corregirfecha;

/**
 * parser y solicitud de canales por medio de una url
 */

public class GestorDispositivos {

    private static GestorDispositivos gestorDispositivos = new GestorDispositivos();

    public static String FORMATO_FECHA = "yyyy-MM-dd'T'HH:mm:ss'-05:00'";

    public static int VALUE_FIELD_NUMBER = 2;
    public static int SWITCH_FIELD = 1;

    public static String SWITCH_FIELD_VALUE_PRENDIDO = "1";
    public static String SWITCH_FIELD_VALUE_APAGADO = "0";
    //rev
    public static boolean AGREGAR = true;

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
        realizarSolicitudGET(new TareaString() {
            @Override
            public void ejecutar(String resultado) {
                ArrayList<Dispositivo> canales = (ArrayList<Dispositivo>) parseArrayCanal(resultado);
                if (canales == null) {
                    canales = new ArrayList<>();
                    Toast.makeText(context, "Compruebe su conexion a internet.", Toast.LENGTH_SHORT).show();
                } else {
                    canales = fitrarCanales(canales);
                    if (canales.isEmpty()) {
                        Toast.makeText(context, "No hay mas Dispositivos disponibles", Toast.LENGTH_SHORT).show();
                    }
                }
                actualizarAdaptador(canales);
            }

            @Override
            public String getString() {
                String url = THINGSPEAK_URL + THINGSPEAK_CHANNELS + URL_JSON + URL_CHAR_QUESTION +
                        THINGSPEAK_API_KEY_STRING + THINGSPEAK_API_KEY + "";
                return url;
            }
        });
    }

    public void recuperarDispositivosBaseDatos(ArrayAdapter adapter) {
        this.adapter = adapter;
        BDDispositivo bdcanal = BDDispositivo.getInstance(context);
        ArrayList listaDispos = bdcanal.leerDispositivos();
        actualizarAdaptador(listaDispos);
    }

    public void eliminarDispositivo(String idDispositivo, ArrayAdapter adapter) {
        this.adapter = adapter;
        BDDispositivo bdcanal = BDDispositivo.getInstance(context);
        bdcanal.deleteDispositivo(idDispositivo);
        ArrayList listaDispos = bdcanal.leerDispositivos();
        actualizarAdaptador(listaDispos);
    }

    public List<Dispositivo> recuperarDispositivosBaseDatos() {
        BDDispositivo bdcanal = BDDispositivo.getInstance(context);
        ArrayList listaDispos = bdcanal.leerDispositivos();
        return listaDispos;
    }

    private void realizarSolicitudGET(final TareaString tareaString) {


        //opcion con volley
        ClienteHttpVolley clienteHttpVolley = ClienteHttpVolley.getInstance(context);

        String url = tareaString.getString();
        Log.i("realizarSolicitudGET",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tareaString.ejecutar(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error en la solicitud http\n revise su conexion o intentelo mas tarde", Toast.LENGTH_LONG).show();
                        Log.e("Error http", "Error en hacer la consulta http. \n" + error.getCause());

                    }
                });
        // Add the request to the RequestQueue stack.
        clienteHttpVolley.addToRequestQueue(stringRequest);

        //requestConVolley(tareaString);

        //opcion con asinctask
        //new ClienteHttpNativo(tareaString).execute();
    }



    private void actualizarAdaptador(ArrayList<Dispositivo> canales) {
        adapter.clear();
        adapter.addAll(canales);
    }


/*
    class ObtenerEntradasCanal implements TareaString {

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
        public String getString() {
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

    public void activarServicioNotificaciones(boolean activo) {
        if(activo){
            context.startService(new Intent(context, ServicioNotificacion.class));
        }else{
            context.stopService(new Intent(context, ServicioNotificacion.class));
        }
    }

    /**
     * Envia un valor correspondiente a un field de un determinado canal al servidor thingSpeak
     */
    private class EnviarValorFieldCanal extends TareaString {

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
                //Toast.makeText(context, "resultado[" + resultado + "] - intentos[" + intentos + "]", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public String getString() {
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
     * @param field
     * @param idCanal
     */
    public void solicitarUltimoValorDeField(final int field, String idCanal, final TareaString callback) {
        //opcion con volley
        final String url = THINGSPEAK_URL + THINGSPEAK_CHANNELS + "/" + idCanal + "/" + THINGSPEAK_FIELDS + "/" + field + "/" + THINGSPEAK_LAST +
                URL_JSON;

        // callback interno
        realizarSolicitudGET(new TareaString() {
            @Override
            public void ejecutar(String resultado) {
                if (resultado.equals("\"-1\"")) {
                    callback.ejecutar("0");
                } else {
                    Log.i("JSONressult", "["+resultado+"]");
                    try {
                        JSONObject obj = (JSONObject) new JSONTokener(resultado).nextValue();
                        String valorf = obj.getString(THINGSPEAK_FIELD + field);
                        //float valor =
                        callback.ejecutar(valorf);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public String getString() {
                return url;
            }
        });
    }

    /**
     *
     * @param api_key
     * @param field
     * @param idCanal
     * @param finicial
     * @param ffinal
     * @param escalaEnMin
     * @param unaTareaList
     * @return
     */
    public String solicitarValoresDeField(
            final String api_key,
            final int field,
            final String idCanal,
            final Calendar finicial,
            final Calendar ffinal,
            final String escalaEnMin,
            final TareaList unaTareaList) {
        //opcion con volley
        TareaString tarea = new TareaString(){

            ArrayList<FeedField> listaValores = (ArrayList) unaTareaList.getList();

            @Override
            public void ejecutar(String resultado) {

                listaValores = (ArrayList<FeedField>) parseListaFeedField(resultado, field,listaValores);

                if(escalaEnMin.equals("1440") || escalaEnMin.equals("daily")){

                    int diaAnterior = 0;
                    DateFormat df = new SimpleDateFormat("dd");
                    List<FeedField> listaTemp = new ArrayList<>();
                    listaTemp.addAll(listaValores);
                    listaValores.clear();

                    FeedField anterior = null;
                    for (FeedField elem:listaTemp) {
                        if(anterior == null){
                            anterior = elem;
                            diaAnterior= Integer.parseInt(df.format(anterior.getFecha()));
                            listaValores.add(elem);
                            continue;
                        }
                        int diaElemen = Integer.parseInt(df.format(elem.getFecha()));
                        if(diaElemen == diaAnterior){

                            anterior.setValor(anterior.getValor()+elem.getValor());
                        }else{
                            anterior = elem;
                            diaAnterior= Integer.parseInt(df.format(anterior.getFecha()));
                            listaValores.add(elem);
                        }
                    }
                }
                unaTareaList.ejecutar(listaValores);
                //Toast.makeText(context, "LISTA: " + listaValores, Toast.LENGTH_LONG).show();
            }


            @Override
            public String getString() {

                String escalaVerdadera = escalaEnMin.equals("1440") || escalaEnMin.equals("daily")? "720": escalaEnMin;
                //ejemplo
                //https://thingspeak.com/channels/175991/fields/1.json?sum=60&start=2016-11-20T15:00:00&end=2016-11-20T19:00:00

                DateFormat df = new SimpleDateFormat(FORMATO_FECHA);
                String url = THINGSPEAK_URL + THINGSPEAK_CHANNELS + "/" + idCanal + "/" + THINGSPEAK_FIELDS + "/" + field +
                        URL_JSON + URL_CHAR_QUESTION + THINGSPEAK_SUM + escalaVerdadera + URL_CHAR_AMEPERSAND +
                        THINGSPEAK_START + df.format(corregirfecha(finicial).getTime())+ URL_CHAR_AMEPERSAND +
                        THINGSPEAK_END + df.format(corregirfecha(ffinal).getTime())+THINGSPEAK_TIMEZONE;

                Log.i("url-1", url);
                return url;
            }
        };
        realizarSolicitudGET(tarea);
        return tarea.getString();
    }

    protected List<FeedField> parseListaFeedField(String respuestaJson, int field,List<FeedField> feedFieldList) {
        if (respuestaJson == null) {
            Toast.makeText(context, "Error al recuperar sockets disponibles", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            JSONObject principal = (JSONObject) new JSONTokener(respuestaJson).nextValue();
            JSONArray array = principal.getJSONArray("feeds");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                DateFormat df = new SimpleDateFormat(FORMATO_FECHA);
                Date fecha = df.parse(obj.getString("created_at"));
                Double valor = obj.optDouble(THINGSPEAK_FIELD + field);
                valor = (!Double.isNaN(valor) && !Double.isInfinite(valor))? valor:0;
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

    private ArrayList<Dispositivo> fitrarCanales(ArrayList<Dispositivo> canales) {

        BDDispositivo bdDispositivo = BDDispositivo.getInstance(context);
        List<Dispositivo> list_canales = bdDispositivo.leerDispositivos();
        if (canales != null && list_canales != null) {
            canales.removeAll(list_canales);
        }

        return canales;
    }


}
