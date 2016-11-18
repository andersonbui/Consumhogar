package com.andersonbuitron.mipruebathingspeakweb.modelos;

import java.io.Serializable;

/**
 * Canal que representa un channel de una cuenta de ThingSpeak
 */

public class Canal implements Serializable {
    private String id;
    private String nombre;
    private String api_key_write;
    private String icono;

    public Canal(String id, String nombre, String api_key_write) {
        this.nombre = nombre;
        this.id = id;
        this.api_key_write = api_key_write;
        this.icono = "";

    }

    public Canal() {
        this.nombre = "";
        this.id = "";
        this.api_key_write = "";
        this.icono = "";
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

    public String getApi_key_write() {
        return api_key_write;
    }

    public void setApi_key_write(String api_key_write) {
        this.api_key_write = api_key_write;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    @Override
    public String toString() {
        return "Canal{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", api_key_write='" + api_key_write + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Canal otrocanal = (Canal)obj;
        return otrocanal.getId().equals(this.getId());
    }
}
