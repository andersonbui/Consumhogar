package com.andersonbuitron.mipruebathingspeakweb.extras;

import java.util.Calendar;

/**
 * Created by debian on 18/11/16.
 */

public class UrlEndPoints {
    /**
     * remoto
     */
    //public static final String THINGSPEAK_API_KEY = "CCX5C0TQRBUBVFR8";
    //public static final String THINGSPEAK_URL = "https://thingspeak.com/";
    /**
     * Local
     */
    public static final String THINGSPEAK_API_KEY = "EP1GTA7CD5X9Z3HQ";
    //public static final String THINGSPEAK_URL = "http://192.168.43.29:3000/";
    public static final String THINGSPEAK_URL = "http://192.168.0.90:3000/";
    public static final int FIELD_DE_VALORES_CONSUMO = 2;
    public static final int ESCALA_SOLICITUD_DATOS = 60;
    public static final String THINGSPEAK_API_KEY_STRING = "api_key=";
    public static final String THINGSPEAK_CHANNELS = "channels";
    public static final String THINGSPEAK_UPDATE = "update";
    public static final String THINGSPEAK_FIELD= "field";
    public static final String THINGSPEAK_FIELDS= "fields";
    public static final String URL_JSON = ".json";
    public static final String URL_CHAR_QUESTION = "?";
    public static final String URL_RESULTS = "results=";
    public static final String URL_CHAR_AMEPERSAND = "&";
    public static final String THINGSPEAK_SUM = "sum=";
    public static final String THINGSPEAK_START = "start=";
    public static final String THINGSPEAK_END = "end=";
    public static final String THINGSPEAK_LAST = "last";
    public static final String THINGSPEAK_TIMEZONE = "&timezone=America%2FBogota";

    public static Calendar configurarInicioFecha(Calendar fecha, int tipo) {
        switch (tipo) {
            case Calendar.YEAR:
                fecha.set(Calendar.MONTH, 0);
            case Calendar.MONTH:
                fecha.set(Calendar.DAY_OF_MONTH, 1);
            case Calendar.DAY_OF_MONTH:
                fecha.set(Calendar.HOUR_OF_DAY, 0);
            case Calendar.HOUR_OF_DAY:
                fecha.set(Calendar.MINUTE, 0);
            default:
                fecha.set(Calendar.SECOND, 0);
                fecha.set(Calendar.MILLISECOND, 1);
        }
        return fecha;
    }

    /**
     * linpia la fecha a partir de un campo dentro de una fecha definida po tipo
     *
     * @param fecha
     * @param tipo
     * @return
     */
    public static Calendar configurarFinFecha(Calendar fecha, int tipo) {
        switch (tipo) {
            case Calendar.YEAR:
                fecha.set(Calendar.MONTH, fecha.getActualMaximum(Calendar.MONTH));
            case Calendar.MONTH:
                fecha.set(Calendar.DAY_OF_MONTH, fecha.getActualMaximum(Calendar.DAY_OF_MONTH));
            case Calendar.DAY_OF_MONTH:
                fecha.set(Calendar.HOUR_OF_DAY, 23);
            case Calendar.HOUR_OF_DAY:
                fecha.set(Calendar.MINUTE, 59);
            default:
                fecha.set(Calendar.SECOND, 59);
                fecha.set(Calendar.MILLISECOND, 0);
        }

        return fecha;
    }
}
