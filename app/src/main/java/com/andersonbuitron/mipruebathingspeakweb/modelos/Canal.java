package com.andersonbuitron.mipruebathingspeakweb.modelos;

/**
 * Canal que representa un channel de una cuenta de ThingSpeak
 */

public class Canal {
    private String id;
    private String nombre;

    public Canal(String id, String nombre) {
        this.nombre = nombre;
        this.id = id;
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
}
