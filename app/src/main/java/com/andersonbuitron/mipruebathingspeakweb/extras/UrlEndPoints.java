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
    public static final String THINGSPEAK_URL = "http://192.168.0.25:3000/";

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

    public static Calendar corregirfecha(Calendar fechain){
        Calendar fecha = (Calendar) fechain.clone();
        fecha.add(Calendar.HOUR_OF_DAY,5);
        return fecha;
    }
}
