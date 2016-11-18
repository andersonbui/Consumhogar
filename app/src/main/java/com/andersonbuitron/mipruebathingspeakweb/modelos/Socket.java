package com.andersonbuitron.mipruebathingspeakweb.modelos;

import java.util.ArrayList;

/**
 * Dispositivo representado por un channel, configurado como tal, de una cuenta ThingSpeak
 */

public class Socket {
    private String id; //channel
    private String nombre;
    private String configuracion;
    private ArrayList<Integer> entradas;

    public Socket(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.configuracion = "";
        this.entradas = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Integer> getEntradas() {
        return entradas;
    }

    public void setEntradas(ArrayList<Integer> entradas) {
        this.entradas = entradas;
    }

    @Override
    public String toString() {
        return "Socket{" +
                "nombre='" + nombre + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
