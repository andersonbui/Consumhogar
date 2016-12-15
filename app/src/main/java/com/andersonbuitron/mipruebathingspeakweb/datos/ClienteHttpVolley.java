package com.andersonbuitron.mipruebathingspeakweb.datos;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by debian on 15/12/16.
 */

public final class ClienteHttpVolley {

    private static ClienteHttpVolley clienteHttpVolley;
    private RequestQueue requestQueue;
    private static Context context;

    private ClienteHttpVolley(Context context) {
        ClienteHttpVolley.context = context;
        this.requestQueue = getRequestQueue();
    }

    public static synchronized ClienteHttpVolley getInstance(Context context){
        if(clienteHttpVolley == null){
            clienteHttpVolley = new ClienteHttpVolley(context);
        }
        return clienteHttpVolley;
    }

    private RequestQueue getRequestQueue(){
        if(requestQueue == null ){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public void addToRequestQueue(Request request){
        getRequestQueue().add(request);
    }
}
